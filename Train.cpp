/**
 * Copyright (c) Facebook, Inc. and its affiliates.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree.
 */

#include <cstdlib>
#include <fstream>
#include <string>
#include <vector>

#include <cereal/archives/json.hpp>
#include <cereal/types/unordered_map.hpp>
#include <flashlight/flashlight.h>
#include <gflags/gflags.h>
#include <glog/logging.h>

#include "common/Defines.h"
#include "common/Dictionary.h"
#include "common/Transforms.h"
#include "common/Utils.h"
#include "criterion/criterion.h"
#include "data/Featurize.h"
#include "module/module.h"
#include "runtime/runtime.h"

#include "data/W2lDataset.h"
#include "data/W2lNumberedFilesDataset.h"

using namespace w2l;


int main(int argc, char** argv) {
  google::InitGoogleLogging(argv[0]);
  google::InstallFailureSignalHandler();
  std::string exec(argv[0]);
  std::vector<std::string> argvs;
  for (int i = 0; i < argc; i++) {
    argvs.emplace_back(argv[i]);
  }
  gflags::SetUsageMessage(
      "Usage: \n " + exec + " train [flags]\n or " + std::string() +
      " continue [directory] [flags]\n or " + std::string(argv[0]) +
      " fork [directory/model] [flags]");

  /* ===================== Parse Options ===================== */
  int runIdx = 1; // current #runs in this path
  std::string runPath; // current experiment path
  std::string reloadPath; // path to model to reload
  std::string runStatus = argv[1];
  int startEpoch = 0;

  std::shared_ptr<fl::Module> network;
  std::shared_ptr<SequenceCriterion> criterion;
  std::unordered_map<std::string, std::string> cfg;
  std::vector<fl::Variable> pretrained_params;

  if (argc <= 1) {
    LOG(FATAL) << gflags::ProgramUsage();
  }

  if (runStatus == "fork") {
    reloadPath = argv[2];
    /* ===================== Create Network ===================== */
    LOG(INFO) << "Network reading pre-trained model from " << reloadPath;
    W2lSerializer::load(reloadPath, cfg, network, criterion);
    pretrained_params = network->params();

    //pre-trained network architecture
    LOG(INFO) << "[Network] is " << network->prettyString();
    LOG(INFO) << "[Criterion] is " << criterion->prettyString();
    LOG(INFO) << "[Network] params size is " << network->params().size();
    LOG(INFO) << "[Network] number of params is " << numTotalParams(network);

    //pre-trained network flags
    auto flags = cfg.find(kGflags);
    if (flags == cfg.end()) {
      LOG(FATAL) << "Invalid config loaded from " << reloadPath;
    }

    LOG(INFO) << "Reading flags from config file " << reloadPath;
    gflags::ReadFlagsFromString(flags->second, gflags::GetArgv0(), true);

    if (argc > 3) {
      LOG(INFO) << "Parsing command line flags";
      LOG(INFO) << "Overriding flags should be mutable when using `fork`";
      gflags::ParseCommandLineFlags(&argc, &argv, false);
    }

    if (!FLAGS_flagsfile.empty()) {
      LOG(INFO) << "Reading flags from file" << FLAGS_flagsfile;
      gflags::ReadFromFlagsFile(FLAGS_flagsfile, argv[0], true);
    }
    runPath = newRunPath(FLAGS_rundir, FLAGS_runname, FLAGS_tag);
  } else {
    LOG(FATAL) << gflags::ProgramUsage();
  }

  af::setMemStepSize(FLAGS_memstepsize);
  af::setSeed(FLAGS_seed);
  af::setFFTPlanCacheSize(FLAGS_fftcachesize);

  maybeInitDistributedEnv(
      FLAGS_enable_distributed,
      FLAGS_world_rank,
      FLAGS_world_size,
      FLAGS_rndv_filepath);

  auto worldRank = fl::getWorldRank();
  auto worldSize = fl::getWorldSize();

  bool isMaster = (worldRank == 0);

  LOG_MASTER(INFO) << "Gflags after parsing \n" << serializeGflags("; ");

  LOG_MASTER(INFO) << "Experiment path: " << runPath;
  LOG_MASTER(INFO) << "Experiment runidx: " << runIdx;

  std::unordered_map<std::string, std::string> config = {
      {kProgramName, exec},
      {kCommandLine, join(" ", argvs)},
      {kGflags, serializeGflags()},
      // extra goodies
      {kUserName, getEnvVar("USER")},
      {kHostName, getEnvVar("HOSTNAME")},
      {kTimestamp, getCurrentDate() + ", " + getCurrentDate()},
      {kRunIdx, std::to_string(runIdx)},
      {kRunPath, runPath}};

  /* ===================== Create Dictionary & Lexicon ===================== */
  Dictionary dict = createTokenDict();
  int numClasses = dict.indexSize();
  LOG_MASTER(INFO) << "Number of classes (network) = " << numClasses;

  DictionaryMap dicts;
  dicts.insert({kTargetIdx, dict});

  LexiconMap lexicon;
  if (FLAGS_listdata) {
    lexicon = loadWords(FLAGS_lexicon, FLAGS_maxword);
  }

  /* =========== Create Network & Optimizers / Reload Snapshot ============ */
  // network, criterion have been loaded before
  std::shared_ptr<fl::FirstOrderOptimizer> netoptim;
  std::shared_ptr<fl::FirstOrderOptimizer> critoptim;
  if (runStatus == "train" || runStatus == "fork") {
    netoptim = initOptimizer(
        network, FLAGS_netoptim, FLAGS_lr, FLAGS_momentum, FLAGS_weightdecay);
    critoptim =
        initOptimizer(criterion, FLAGS_critoptim, FLAGS_lrcrit, 0.0, 0.0);
  }
  LOG_MASTER(INFO) << "[Network Optimizer] " << netoptim->prettyString();
  LOG_MASTER(INFO) << "[Criterion Optimizer] " << critoptim->prettyString();

  /* ===================== Meters ===================== */
  

  /* ===================== Logging ===================== */
  

  /* ===================== Create Dataset ===================== */
  auto trainds = createDataset(
      FLAGS_train, dicts, lexicon, FLAGS_batchsize, worldRank, worldSize);


  /* ===================== Hooks ===================== */

  double gradNorm = 1.0 / (FLAGS_batchsize * worldSize);

  auto train = [gradNorm,
                pretrained_params,
                &startEpoch](
                   std::shared_ptr<fl::Module> ntwrk,
                   std::shared_ptr<SequenceCriterion> crit,
                   std::shared_ptr<W2lDataset> trainset,
                   std::shared_ptr<fl::FirstOrderOptimizer> netopt,
                   std::shared_ptr<fl::FirstOrderOptimizer> critopt,
                   double initlr,
                   double initcritlr,
                   bool clampCrit,
                   int nepochs) {
    fl::distributeModuleGrads(ntwrk, gradNorm);
    fl::distributeModuleGrads(crit, gradNorm);

    // synchronize parameters across processes
    fl::allReduceParameters(ntwrk);
    fl::allReduceParameters(crit);


    int64_t curEpoch = startEpoch;
    int64_t sampleIdx = 0;
    while (curEpoch < nepochs) {
      double lrScale = std::pow(FLAGS_gamma, curEpoch / FLAGS_stepsize);
      netopt->setLr(lrScale * initlr);
      critopt->setLr(lrScale * initcritlr);

      ++curEpoch;
      ntwrk->train();
      crit->train();

      af::sync();
      
      LOG_MASTER(INFO) << "Epoch " << curEpoch << " started!";

      //the size of trainset is just 1.
      auto pre_sample = trainset->get(0); //make noises for one audio sample
      int numNoise = 5000; //make 1000 noise sub-samples for the audio sample
      std::vector<float> Yloss(numNoise); //loss written into Yloss
      std::ofstream Yfile("/root/w2l/CTC/loss.txt", std::ios::out);
      //std::vector<float> firGradnorm(numNoise);
      //std::ofstream firGradnormFile("/root/w2l/aboutM/firGradnorm.txt", std::ios::out);
      //std::vector<float> secGradnorm(numNoise);
      //std::ofstream secGradnormFile("/root/w2l/aboutM/secGradnorm.txt", std::ios::out);
      //std::vector<float> totGradnorm(numNoise);
      //std::ofstream totGradnormFile("/root/w2l/aboutM/totGradnorm.txt", std::ios::out);

      af::dim4 noiseDims = pre_sample[kFftIdx].dims(); //2K x T x FLAGS_channels x batchSz
      auto m = af::constant(0.1, noiseDims);
      //auto m = af::constant(0.1,noiseDims);
      //auto m=fl::normal(noiseDims,0.002,0.1).array();
      float mylr = 0.001;

      //the previous network's output f*
      fl::Variable preOutput; 
      //W2lSerializer::load("/root/w2l/rawEmission.bin", preOutput);

      //pre_sample[kInputIdx] dims: T x K(257) x 1 x 1
      LOG_MASTER(INFO) << "pre_sample[kInputIdx] dims: " << pre_sample[kInputIdx].dims();
      //pre_sample[kFftIdx] dims: 2K(514) x T x 1 x 1
      LOG_MASTER(INFO) << "pre_sample[kFftIdx] dims: " << pre_sample[kFftIdx].dims();
      const float fftmean = af::mean<float>(pre_sample[kFftIdx]);
      const float fftstdev = af::stdev<float>(pre_sample[kFftIdx]);
      //LOG_MASTER(INFO) << af::toString("pre_sample fft's 6 values :", pre_sample[kFftIdx](af::seq(6)));
      LOG_MASTER(INFO) << "fft mean is:" << af::mean<float>(pre_sample[kFftIdx]);//-0.12
      LOG_MASTER(INFO) << "fft stdev is:" << af::stdev<float>(pre_sample[kFftIdx]);//4268.81
      //LOG_MASTER(INFO) << "dft mean is:" << af::mean<float>(pre_sample[kInputIdx]);//2136.15
      //LOG_MASTER(INFO) << "dft stdev is:" << af::stdev<float>(pre_sample[kInputIdx]);//5646.45

      std::ofstream preinput("/root/w2l/CTC/preFft.txt");
      if(preinput.is_open())
      {
        preinput << af::toString("pre_fft values:",pre_sample[kFftIdx]);
        preinput.close();
      }
      //using network to generate preOutput 
      auto prefinalinput=pre_sample[kInputIdx];
      const float inputmean=af::mean<float>(pre_sample[kInputIdx]);
      const float inputstdev=af::stdev<float>(pre_sample[kInputIdx]);
      prefinalinput= (prefinalinput-inputmean)/inputstdev;
      fl::Variable pretruefinalinput(prefinalinput,false);
  
      ntwrk->eval();
      crit->eval();
      preOutput = ntwrk->forward({pretruefinalinput}).front();
      af::sync();
      af::array zerowgt = af::identity(31,31);
      zerowgt(30, 30) = 0;
      auto addpreOutput = fl::Variable(af::matmul(zerowgt, preOutput.array()), false);
      std::ofstream preOutFile("/root/w2l/CTC/preOutput.txt");
      if(preOutFile.is_open())
      {
	preOutFile << af::toString("preOutput is:", addpreOutput.array());
	preOutFile.close();
      }
      
      
      ntwrk->train();
      crit->train();

      for (int i = 0; i < numNoise; i++) {
        LOG(INFO) << "=================noise sample " << i << "==================";
        // meters
        af::sync();
        
        if (af::anyTrue<bool>(af::isNaN(pre_sample[kInputIdx])) ||
            af::anyTrue<bool>(af::isNaN(pre_sample[kTargetIdx]))) {
          LOG(FATAL) << "pre_sample has NaN values";
        }
//////////////////////////////////////////////////////////////////////////////////////////////////////
	//auto epsilon = (af::randn(noiseDims)) * 4268; 
        auto epsilon = fl::normal(noiseDims,fftstdev,0).array(); //add noises
	LOG(INFO)<<"epsilon mean is:"<<af::mean<float>(epsilon);
	LOG(INFO)<<"epsilon stdev is:"<<af::stdev<float>(epsilon);
	//save last iter epsilon parameter:
	if (i == numNoise-1)
	{
	   std::ofstream epsfile("/root/w2l/CTC/epsilon.txt");
	   if(epsfile.is_open())
  	   {
	      epsfile << af::toString("epsilon values:", epsilon);
              epsfile.close();
	   }
	}
        ///////////////////////////////////////////////////////////////////////////////////////////////
	auto rawinput = pre_sample[kFftIdx] + m * epsilon;
	//LOG(INFO)<<af::toString("epsilon 6 values:", epsilon(af::seq(6)));
	//LOG(INFO)<<af::toString("m 6 values:", m(af::seq(6)));
	//LOG(INFO)<<af::toString("rawinput 6 values:",rawinput(af::seq(6)));
        

        int T = noiseDims[1];
        int K = noiseDims[0]/2;
        af::array absinput(af::dim4(K, T, noiseDims[2], noiseDims[3]));
        af::array backinput(noiseDims);
        
        
        //LOG(INFO) << "m_epsilon mean :" << af::mean<float>(m*epsilon);
        //LOG(INFO) << "m_epsilon stdev :" << af::stdev<float>(m*epsilon);
        
        for (size_t j = 0; j < 2*K; j=j+2)
        {
            auto fir = rawinput(j, af::span, af::span, af::span);
            //LOG(INFO) << "fir row(i) dims is :" << fir.array().dims() << " " << af::toString("row(i) first value is ", fir.array()(0));
            auto sec = rawinput(j+1, af::span, af::span, af::span);
            //note shallow copy in fl::Variable
            auto temp = af::sqrt(fir * fir + sec * sec);
            absinput(j/2, af::span, af::span, af::span) =  temp;
            backinput(j, af::span, af::span, af::span) = temp;
            backinput(j+1, af::span, af::span, af::span) = temp;
        }

        //T x K x FLAGS_channels x batchSz
        af::array trInput = af::transpose(absinput);

        // dft kInputIdx not normalized
        //LOG(INFO) << "dft abs mean :" << af::mean<float>(absinput);
        //LOG(INFO) << "dft abs stdev :" << af::stdev<float>(absinput);

        // normalization
        auto mean = af::mean<float>(trInput); // along T and K two dimensions 1x1x1x1
        auto stdev = af::stdev<float>(trInput); //1 scalar
        auto finalInput = (trInput - mean) / stdev;
        fl::Variable trueInput(finalInput, true);
        
        auto indif = af::mean<float>(trInput - pre_sample[kInputIdx]);
        LOG(INFO) << "dft input difference mean is:" << indif;
        /*
        std::ofstream exfile("/home/zd/beforenorm.txt");
        if(exfile.is_open())
        {  
           exfile << af::toString("before norm", finalInput.array());
           exfile.close();
        }
        */

        // forward
        auto output = ntwrk->forward({trueInput}).front();
        af::array wgt = af::identity(31, 31); // numClasses are 31 tokens
	wgt(30, 30) = 0;
        auto addweight = fl::Variable(wgt, true);
	auto addoutput = fl::matmul(addweight, output);

        af::sync();
	if(i == numNoise-1)
	{
	    std::ofstream nowOutFile("/root/w2l/CTC/lastOutput.txt");
	    if(nowOutFile.is_open())
	    {
	       nowOutFile<<af::toString("lastOutput is:", addoutput.array());
	       nowOutFile.close();
	    }
        }
        
        //LOG(INFO) << "network forward output dims is "<< output.array().dims();
        //LOG(INFO) << "load rawEmission preOutput dims is :" << preOutput.array().dims() ;
	float lambda = 10;
        //float lambda = 100;
        auto f_L2 = fl::norm(addpreOutput - addoutput, {0,1});
        auto m_L2 = af::norm(m); //double
        auto myloss = f_L2 * f_L2;
        //auto firloss = fl::MeanSquaredError();
        //auto myloss = firloss(output, preOutput);

        float totloss = myloss.scalar<float>() - lambda * std::log(m_L2 * m_L2);

        LOG(INFO) << "f star norm is:" << af::norm(preOutput.array());
        LOG(INFO) << "f now norm is:" << af::norm(output.array());
        LOG(INFO) << "loss - f difference is :" << myloss.scalar<float>();
        LOG(INFO) << "loss - logm is :" << std::log(m_L2 * m_L2);
        LOG(INFO) << "loss is:" << totloss;
        Yfile << totloss << std::endl;

        af::sync();
       

        if (af::anyTrue<bool>(af::isNaN(myloss.array()))) {
          LOG(FATAL) << "Loss has NaN values";
        }

        // clear the gradients for next iteration
        netopt->zeroGrad();
        critopt->zeroGrad();
        addweight.zeroGrad();

        //Compute gradients using backprop
        myloss.backward();
        af::sync();
	//Print output's Grad
	if(i == numNoise-1)
	{
            std::ofstream outputGradFile("/root/w2l/CTC/outputGrad.txt");
	    if(outputGradFile.is_open())
	    {
	        outputGradFile << af::toString("output Grad is:", output.grad().array());
                outputGradFile.close();
	    }
        }

        if (FLAGS_maxgradnorm > 0) {
          auto params = ntwrk->params();
          if (clampCrit) {
            auto critparams = crit->params();
            params.insert(params.end(), critparams.begin(), critparams.end());
          }
          fl::clipGradNorm(params, FLAGS_maxgradnorm);
        }

        //critopt.step();
        //netopt.step();
        //update parameter m

        auto sigma2 = stdev * stdev;
        auto dy = trueInput.grad().array(); //T x K
        auto dsigma2 = af::sum<float>(dy * (trInput - mean) * (-0.5) * std::pow(sigma2, -1.5));
        auto dmu = af::sum<float>(dy * (-1.0/std::pow(sigma2, 0.5))) + af::sum<float>(-2 * (trInput - mean)) * dsigma2 / (T * K);
        auto dx = dy / std::pow(sigma2, 0.5) + dsigma2 * 2 * (trInput - mean) / (T * K) + dmu / (T * K); 

        af::array xGrad = af::transpose(dx); // K x T x 1 x 1
        auto midGrad = epsilon * epsilon * m + epsilon * pre_sample[kFftIdx];
        auto xGradm = midGrad / backinput; //2K x T x 1 x 1
        af::array mGrad = af::constant(0, noiseDims);

        for(size_t j=0; j< 2*K; j=j+2) {
          mGrad(j, af::span, af::span, af::span) = xGrad(j/2,af::span,af::span,af::span) * xGradm(j,af::span,af::span,af::span); 
          mGrad(j+1, af::span, af::span, af::span) = xGrad(j/2,af::span,af::span,af::span) * xGradm(j+1, af::span,af::span,af::span);
        }
	
        
        mGrad = mGrad - 2 * lambda * m / (m_L2 * m_L2);

        m = m - mylr * mGrad;
        
        
        //network params whether to be changed
        fl::MSEMeter mymeter;
        auto psize = ntwrk->params().size();
        for(int j=0 ; j<psize; j++) {
          mymeter.add(ntwrk->param(j).array(), pretrained_params[j].array());
        }
        LOG(INFO) << "the network params change " << mymeter.value();        
      }


      af::sync();
      if (FLAGS_reportiters == 0) {
        //runValAndSaveModel(curEpoch, netopt->getLr(), critopt->getLr());
        //std::string mpath = "/root/w2l/aboutM/last_m.bin";
        //W2lSerializer::save(mpath, m);

        std::ofstream mfile("/root/w2l/CTC/lastm.txt");
        if(mfile.is_open())
        {
          mfile << af::toString("last m is:", m);
          mfile.close();
        }
      }
    }
  };

  /* ===================== Train ===================== */
  train(
      network,
      criterion,
      trainds,
      netoptim,
      critoptim,
      FLAGS_lr,
      FLAGS_lrcrit,
      true /* clampCrit */,
      FLAGS_iter);

  LOG_MASTER(INFO) << "Finished my training";
  return 0;
}
