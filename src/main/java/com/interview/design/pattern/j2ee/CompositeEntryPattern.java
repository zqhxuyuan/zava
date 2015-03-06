package com.interview.design.pattern.j2ee;

/**
 * Created_By: stefanie
 * Date: 14-12-3
 * Time: 下午6:22
 *
 * Composite Entity pattern is used in EJB persistence mechanism.
 * A Composite entity is an EJB entity bean which represents a graph of objects.
 * When a composite entity is updated, internally dependent objects beans get updated automatically
 * as being managed by EJB entity bean.
 *
 * Following are the participants in Composite Entity Bean.
 * Composite Entity - It is primary entity bean.It can be coarse grained or can contain a coarse grained object
 *                    to be used for persistence purpose.
 * Coarse-Grained Object - This object contains dependent objects. It has its own life cycle and also manages life
 *                         cycle of dependent objects.
 * Dependent Object - Dependent objects is an object which depends on Coarse-Grained object for its persistence lifecycle.
 * Strategies - Strategies represents how to implement a Composite Entity.
 */
public class CompositeEntryPattern {

    static class DependentObject1 {

        private String data;

        public void setData(String data){
            this.data = data;
        }

        public String getData(){
            return data;
        }
    }

    static class DependentObject2 {

        private String data;

        public void setData(String data){
            this.data = data;
        }

        public String getData(){
            return data;
        }
    }

    static class CoarseGrainedObject {
        DependentObject1 do1 = new DependentObject1();
        DependentObject2 do2 = new DependentObject2();

        public void setData(String data1, String data2){
            do1.setData(data1);
            do2.setData(data2);
        }

        public String[] getData(){
            return new String[] {do1.getData(),do2.getData()};
        }
    }

    static class CompositeEntity {
        private CoarseGrainedObject cgo = new CoarseGrainedObject();

        public void setData(String data1, String data2){
            cgo.setData(data1, data2);
        }

        public String[] getData(){
            return cgo.getData();
        }
    }

    static class Client {
        private CompositeEntity compositeEntity = new CompositeEntity();

        public void printData(){
            for (int i = 0; i < compositeEntity.getData().length; i++) {
                System.out.println("Data: " + compositeEntity.getData()[i]);
            }
        }

        public void setData(String data1, String data2){
            compositeEntity.setData(data1, data2);
        }
    }

    public static void main(String[] args) {
        Client client = new Client();
        client.setData("Test", "Data");
        client.printData();
        client.setData("Second Test", "Data1");
        client.printData();
    }
}
