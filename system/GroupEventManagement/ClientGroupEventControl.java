package GroupEventControl;

import EventManagement.ClientEventControl;
import EventManagement.Event;
import EventManagement.EventList;

import java.util.GregorianCalendar;

public class ClientGroupEventControl  {

    int currentGroupID;
    int currentGroupEventID;
    GroupEventList groupEventList;

    public ClientGroupEventControl(int cgID){
        currentGroupID = cgID;
    }



    // 通过currentUserID向服务器找到当前user的eventList
    public GroupEventList getGroupEventList(){

        // ！！！！！！！！！！！！！！
        return null;
    }



    // 增加一个group event
    public void addGroupEvent(GroupEvent groupEvent){

        groupEventList.add(groupEvent);
        currentGroupEventID += 1;

    }



    // 删除一个group event
    public int deleteGroupEvent(int eventID){
        groupEventList.sortByEventID();
        int index = groupEventList.findBinary(eventID);

        if (index != -1){
            groupEventList.delete(index);
            return 0;
        }
        else{
            System.out.println("EventID not found");
            return 1;
        }

    }



    // 修改一个group event,这里默认是从当前group查找一个event，所以不输入groupID
    public int editGroupEvent(String description, int eventID, int [] dutyUserIDs, int beginYear, int beginMonth, int beginDate, int beginHour, int beginMin,
                         int endYear, int endMonth, int endDate, int endHour, int endMin, boolean wProcess){
        groupEventList.sortByEventID();
        int index = groupEventList.findBinary(eventID);

        if (index != -1){

            groupEventList.get(index).setDescription(description);
            groupEventList.get(index).setBeginTime( new GregorianCalendar(beginYear, beginMonth - 1, beginDate, beginHour, beginMin));
            groupEventList.get(index).setEndTime(new GregorianCalendar(endYear, endMonth - 1, endDate, endHour, endMin));
            groupEventList.get(index).setWhetherProcess(wProcess);
            groupEventList.get(index).setDutyUserIDs(dutyUserIDs);

            return 0;
        }
        else{

            System.out.println("EventID not found.");
            return 1;

        }
    }



    // 将现在的groupEventList同步到服务器
    public void updateGroupEvent(){

        // ！！！！！！！！！！！

    }



    // 测试
    public static void main(String[] args){
        ClientGroupEventControl crtl = new ClientGroupEventControl(25);
        GroupEventList groupEventList = new GroupEventList();
        crtl.groupEventList = groupEventList;
        // 使用时要保证eventID不在list中，建议使用currentEventID创建新的event
        try {
            int [] userIDs = {3, 4, 5};
            for (int i = 10; i > 0; i -- ){
                crtl.addGroupEvent(new GroupEvent(String.format("%d-%d-%d",i,i,i), i, 0, userIDs, i, i, i + 1, i + 1, true));
            }

            for (int i = 0; i < crtl.groupEventList.length(); i ++){
                System.out.println("groupeventID: " + crtl.groupEventList.get(i).getEventID());
                System.out.println("groupeventDescription: " + crtl.groupEventList.get(i).getDescription());
                System.out.println("groupeventBeginTime: " +crtl.groupEventList.get(i).getBeginTime().getTime().toString());
                System.out.println("groupeventEndTime: " +crtl.groupEventList.get(i).getEndTime().getTime().toString());
                System.out.println("groupeventGroupID: "+ crtl.groupEventList.get(i).getGroupID());
                System.out.print("userIDs: ");
                for (int j : crtl.groupEventList.get(i).dutyUserIDs){
                    System.out.print(j);
                }
                System.out.println();
                System.out.println();
            }

            crtl.deleteGroupEvent(5);
            crtl.groupEventList.sortByEventID();
            int [] userIDs2 = {100, 200, 300};
            crtl.editGroupEvent("yyy",6, userIDs2,2019,8,19,0,32,2019,9,12,4,2,true);

            for (int i = 0; i < crtl.groupEventList.length(); i ++){
                System.out.println("groupeventID: " + crtl.groupEventList.get(i).getEventID());
                System.out.println("groupeventDescription: " + crtl.groupEventList.get(i).getDescription());
                System.out.println("groupeventBeginTime: " +crtl.groupEventList.get(i).getBeginTime().getTime().toString());
                System.out.println("groupeventEndTime: " +crtl.groupEventList.get(i).getEndTime().getTime().toString());
                System.out.println("groupeventGroupID: "+ crtl.groupEventList.get(i).getGroupID());
                System.out.print("userIDs: ");
                for (int j : crtl.groupEventList.get(i).dutyUserIDs){
                    System.out.print(j);
                }
                System.out.println();
                System.out.println();
            }
        }catch(Exception ex){
            System.out.println(ex.getMessage());
        }
    }

}
