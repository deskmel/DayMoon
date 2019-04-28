package GroupEventControl;

import EventManagement.Event;

public class GroupEvent extends Event {

    int groupID; // 标注当前groupEvent属于哪个组，可能没有用
    int [] dutyUserIDs; // 标注当前groupEvent的用户们

    GroupEvent(String str, int eID, int grpID, int [] dUID, int beginYear, int beginMonth, int beginDate, int beginHour, int beginMin,
          int endYear, int endMonth, int endDate, int endHour, int endMin, boolean wProcess) throws Exception{

        super(str, eID, beginYear, beginMonth, beginDate, beginHour, beginMin, endYear, endMonth, endDate, endHour, endMin, wProcess);
        groupID = grpID;
        dutyUserIDs = dUID;
    }



    GroupEvent(String str, int eID, int grpID, int [] dUID, int beginHour, int beginMin,
               int endHour, int endMin, boolean wProcess) throws Exception{

        super(str, eID, beginHour, beginMin, endHour, endMin,  wProcess);
        groupID = grpID;
        dutyUserIDs = dUID;
    }



    public int getGroupID(){
        return groupID;
    }



    public int [] getDutyUserIDs(){
        return dutyUserIDs;
    }



    // 最好不更改
    public void setGroupID(int groupID){
        this.groupID = groupID;
    }



    public void setDutyUserIDs(int [] dutyUserIDs){
        this.dutyUserIDs = dutyUserIDs;
    }

}
