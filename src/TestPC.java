
import static jdk.nashorn.internal.objects.NativeArray.push;


/*
* 1、生产者产生资源往池子里添加，前提是池子没有满，如果池子满了，则生产者暂停生产，直到自己的生成能放下池子。
* 2、消费者消耗池子里的资源，前提是池子的资源不为空，否则消费者暂停消耗，进入等待直到池子里有资源数满足自己的需求。
* */

public class TestPC {
    public static void main(String[] args) {
        Store store=new Store(); //创建一个仓库对象
        Productor productor = new Productor(store); //创建一个生产者对象并获取仓库对象
        Consumer consumer = new Consumer(store);    //创建一个消费者对象并获取仓库对象
        new Thread(productor).start();//线程进入就绪状态
        new Thread(consumer).start(); //线程就绪就绪状态
    }
}

//生产者
class Productor implements Runnable{
    Store store;       //生产者需要获取仓库对象用以存储产品
    Productor(Store store){
        this.store=store;
    }
    @Override
    public void run() {
        for (int i = 1; i <= 100; i++) {
        //将生产产品存入仓库
        Product product=new Product(i);
        store.push(product);
    }
}
}
//消费者
class Consumer implements Runnable{
    Store store;
    Consumer(Store store){
        this.store=store;
    }
    @Override
    public void run() {
        for (int i = 1; i <= 100; i++){
            //取出一个产品
            store.pop();
        }
    }
}

//产品
class Product{
    //给产品一个id属性
    int id;
    public Product(int id){
        this.id=id;
    }

}
//仓库缓存区(管程)
class Store{
    Product []products=new Product[10];//定义一个容纳10个产品的对象数组
    static int count=0;//仓库产品数量计数，初始为0

    public synchronized void push(Product product){//synchronized关键字会加锁，每次只能有一个线程访问
        //判断仓库是否已满，若满则调用wait()方法令线程停止，使生产者停止产品的生产
        if (count>=10)
        {
            try {
                wait();//线程等待.与此同时会解锁
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        products[count]=product;
        count++;
        System.out.println("生产者生产了第"+product.id+"个产品");

        this.notifyAll();//唤醒所有线程，所有等待线程进入锁池去竞争，优先级高的线程会竞争成功
    }
    public synchronized void pop(){
        //判断仓库是否存在产品，若无则调用wait()方法令线程停止，使消费者停止产品的获取
        if (count==0){
            try {
                wait();//线程等待
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        count--;
        Product product=products[count];
        System.out.println("消费者消费了第"+product.id+"个产品");

        this.notifyAll();//唤醒所有线程，所有等待线程进入锁池去竞争
    }


}
