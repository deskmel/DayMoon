package GroupEventControl;

import EventManagement.Event;

import java.security.acl.Group;
import java.util.ArrayList;
import java.util.Collections;

public class GroupEventList{

    ArrayList<GroupEvent> groupEventRecord;

    public GroupEventList(){
        groupEventRecord = new ArrayList<GroupEvent>();
    }



    public void add(GroupEvent groupEvent){
       groupEventRecord.add(groupEvent);
    }



    public void delete(int eventID){
        groupEventRecord.remove(eventID);
    }



    public int length(){
        return groupEventRecord.size();
    }



    public GroupEvent get(int index){
        return groupEventRecord.get(index);
    }



    // 找对应eventID的索引
    public int find(int eventID){
        for (int i = 0; i < groupEventRecord.size(); i++){
            if (groupEventRecord.get(i).getEventID() == eventID){

                return i;
            }
        }
        return -1;
    }



    // 找对应eventID的索引，二分查找，要先排序
    public int findBinary(int eventID){

        int start = 0, end = groupEventRecord.size() - 1;
        int middle;

        while (start <= end){
            middle = (start + end) / 2;
            if (groupEventRecord.get(middle).getEventID() > eventID){
                end = middle - 1;
            }
            else if (groupEventRecord.get(middle).getEventID() < eventID){
                start = middle + 1;
            }
            else {
                return middle;
            }
        }
        return -1;
    }



    // 按eventID排序
    public void sortByEventID(){
        Collections.sort(groupEventRecord);
    }



    // 测试
    public static void main(String [] args){
        GroupEventList gl = new GroupEventList();
        int [] ids1 = {2, 33, 444, 5555};
        int [] ids2 = {666, 777, 888, 999};
        try {
            // 一般同时操作的GroupEvent具有一样的groupID
            gl.add(new GroupEvent("first issue", 2, 0, ids1, 2019, 2, 28, 17, 59,
                    2019, 4, 27, 0, 0, true));
            gl.add(new GroupEvent("first issue", 1, 0, ids2, 2019, 2, 27, 17, 51,
                    2019, 4, 30, 0, 0, true));

            for (int i = 0; i < gl.length(); i ++){
                System.out.println("eventID: " + gl.get(i).getEventID());
            }
            System.out.println();
            gl.sortByEventID();
            System.out.println(gl.findBinary(0));
            int [] ids = gl.get(1).getDutyUserIDs();

            for (int i: ids){
                System.out.print(i);
            }

            for (int i = 0; i < gl.length(); i ++){
                System.out.println("eventID: " + gl.get(i).getEventID());
            }

        }catch (Exception ex){
            System.out.println(ex.getMessage());
        }

    }
}
