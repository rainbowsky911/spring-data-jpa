package com.example.demo;

import com.example.demo.itcast.LinkMan;
import com.example.demo.itcast.MyCustomer;
import com.example.demo.itcast.MyCustomer2;
import com.example.demo.itcast.dao.CustomerDao;
import com.example.demo.itcast.dao.CustomerDao2;
import com.example.demo.itcast.dao.LinkManDao;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CustomerTest {


    @Autowired
    private CustomerDao customerDao;


    @Autowired
    private CustomerDao2 customerDao2;

    @Autowired
    private LinkManDao linkManDao;


    @Test
    public void testQueryAll() {
        customerDao.findAll().forEach(System.out::println);
    }


    @Test
    @Transactional
    @Rollback(value = false)
    // @Rollback(value = false)
    public void testSave() {
        MyCustomer myCustomer = new MyCustomer();
        myCustomer.setCustName("百度");


        LinkMan linkMan = new LinkMan();
        linkMan.setLkmName("小李");

        myCustomer.getLinkMans().add(linkMan);

        customerDao.save(myCustomer);
        linkManDao.save(linkMan);

    }


    @Test
    @Transactional
    @Rollback(value = false)
    public void testSave2() {
        MyCustomer myCustomer = new MyCustomer();
        myCustomer.setCustName("百度");

        LinkMan linkMan = new LinkMan();
        linkMan.setLkmName("小李");

        linkMan.setCustomer(myCustomer);

        customerDao.save(myCustomer);
        linkManDao.save(linkMan);

    }


    /***
     * 级联删除*/
    @Test
    public void testDelete() {
        customerDao.delete(99L);
    }

    @Test
    public void testCascadeAdd() {
        MyCustomer myCustomer = new MyCustomer();
        myCustomer.setCustName("新浪1");

        LinkMan linkMan = new LinkMan();
        linkMan.setLkmName("小李1");

        //注意这里一对多的添加、 多对一的添加都要，不然会缺少外键
        linkMan.setCustomer(myCustomer);
        myCustomer.getLinkMans().add(linkMan);

        customerDao.save(myCustomer);
    }

    @Test
    public void testSaveUpdate() {
        MyCustomer dbdoMain = customerDao.findOne(101L);
        dbdoMain.setCustName("腾讯");
        customerDao.save(dbdoMain);

    }


    @Test
    public void testCount() {
        //   long count = customerDao.count();
        //     System.out.println(count);

        customerDao.findByCustNameLike("百度%").stream().forEach(System.out::println);
    }


    /***
     * 动态查询
     * */
    @Test
    public void testSpec() {

        List<MyCustomer> all = customerDao.findAll(new Specification<MyCustomer>() {
            @Override
            public Predicate toPredicate(Root<MyCustomer> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {


                //1 获取属性
                Path custLevel = root.get("custLevel");
                Path custAddress = root.get("custAddress");
                //2 构建查询方式
                Predicate p1 = criteriaBuilder.gt(custLevel, 1);
                Predicate p2 = criteriaBuilder.notEqual(custAddress, "china");
                //将多个查询条件组合到一起。  且的关系 、 或的关系
                Predicate predicate = criteriaBuilder.and(p1, p2);

                return predicate;
            }
        });

        System.out.println(all);
    }


    /***
     * 动态查询
     * */
    @Test
    public void testSpec2() {
        Sort sort = new Sort(Sort.Direction.DESC, "custId");

        List<MyCustomer> all = customerDao.findAll(new Specification<MyCustomer>() {
            @Override
            public Predicate toPredicate(Root<MyCustomer> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {

                //1 获取属性
                Path custName = root.get("custName");
                //2 构建查询方式
                Predicate p1 = criteriaBuilder.like(custName, "百度");

                //指定了泛型
                // Path<String> custName = root.get("custName");
                // Predicate p2 = criteriaBuilder.like(custName.as(String.class), "腾讯");

                //将多个查询条件组合到一起。  且的关系 、 或的关系
                Predicate predicate = criteriaBuilder.and(p1);

                return predicate;
            }
        }, sort);

        System.out.println(all);
    }


    /***
     * 分页*/
    @Test
    public void testPageable() {
        Specification specification = null;
        Pageable pageable = new PageRequest(0, 2);
        Page<MyCustomer> page = customerDao.findAll(pageable);
        System.out.println(page.getTotalElements());
    }

    /****
     * JPA使用Specifiaction 进行多对一查询
     * */

    @Test
    public void testManytoOneBySpec() {

//        List<MyCustomer> list = customerDao.findAll(new Specification<MyCustomer>() {
//            @Override
//            public Predicate toPredicate(Root<MyCustomer> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
//
//
////
////                CriteriaQuery<MyCustomer> q1 = cb.createQuery(MyCustomer.class);
////                //连接的时候，要以声明的根查询对象（这里是root，也可以自己创建）进行join
////                //Join<Z,X>是Join生成的对象，这里的Z是被连接的对象，X是目标对象，
////                //  连接的属性字段是被连接的对象在目标对象的属性，这里是我们在MyOrder内声明的customer
////                //join的第二个参数是可选的，默认是JoinType.INNER(内连接 inner join)，也可以是JoinType.LEFT（左外连接 left join）
////                Join<LinkMan, MyCustomer> myOrderJoin = root.join("linkMans", JoinType.LEFT);
////                q1.select(myOrderJoin).where(cb.equal(root.get("custName"), "百度"));
////                //通过getRestriction获得Predicate对象
////                Predicate p1 = q1.getRestriction();
//
//
////                CriteriaQuery<MyCustomer> q1 = cb.createQuery(MyCustomer.class);
////                q1.select(myOrderJoin).where(
////                        cb.equal(root.get("custName"), "百度"),
////                        cb.equal(root.join("linkMans",JoinType.LEFT).get("lkmName"), "小李")
////                        );
////                Predicate p1 = q1.getRestriction();
//
//
//                List<Predicate> preList = new ArrayList<>();
//                preList.add(cb.equal(root.get("custName"), "百度"));
//
//                Join<LinkMan, MyCustomer> linkMans = root.join("linkMans", JoinType.LEFT);
//                // preList.add(cb.equal(join.get("linkMans"), "小李"));
//                Predicate[] pres = new Predicate[preList.size()];
//                return query.where(preList.toArray(pres)).getRestriction();
//            }
//        });
//        list.stream().forEach(System.out::println);


        //TODO 一对多查询会报错。

        /***
         *
         * 多对一查询可以
         * */

        linkManDao.findAll(new Specification<LinkMan>() {
            @Override
            public Predicate toPredicate(Root<LinkMan> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder cb) {

                List<Predicate> preList = new ArrayList<>();

                preList.add(cb.equal(root.get("lkmName"), "张三"));


                Join<LinkMan, MyCustomer> join = root.join("customer", JoinType.INNER);
                preList.add(cb.equal(join.get("custName"), "百度"));

                Predicate[] pres = new Predicate[preList.size()];
                return criteriaQuery.where(preList.toArray(pres)).getRestriction();
            }
        }).stream().forEach(System.out::println);
        ;
    }

    /****
     * JPA使用Specifiaction 进行一对多查询
     * Unable to locate Attribute with the the given name [***] on this ManagedType 失败的原因在于要遵循驼峰命名，建议全部小写！！！
     *
     * */

    // 参考
    /*
    https://zhuanlan.zhihu.com/p/311801352

    https://www.bbsmax.com/A/kmzLKyeBdG/

    https://stackoverflow.com/questions/53820207/illegalstateexception-using-criteriabuilder-for-getting-a-list-in-java
    *
    * */
    @Test
    public void testOneToManyBySpec() {
//        Specification spec = (Specification) (root, criteriaQuery, criteriaBuilder) -> {
//            Join<MyCustomer2, LinkMan> join = root.join("set", JoinType.INNER);
//            Predicate p = criteriaBuilder.equal(join.get("lkmName"), "张三");
//            return p;
//        };
//        List<MyCustomer2> list = customerDao2.findAll(spec);
//        list.stream().forEach(System.out::println);

        customerDao2.findAll(new Specification<MyCustomer2>() {
            @Override
            public Predicate toPredicate(Root<MyCustomer2> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder cb) {

                List<Predicate> preList = new ArrayList<>();

                preList.add(cb.equal(root.get("custName"), "百度"));


                Join<MyCustomer2, LinkMan> join = root.join("set", JoinType.LEFT);
                preList.add(cb.equal(join.get("lkmName"), "张三"));

                Predicate[] pres = new Predicate[preList.size()];
                return criteriaQuery.where(preList.toArray(pres)).getRestriction();
            }
        }).stream().forEach(System.out::println);


//        customerDao2.findAll(new Specification<MyCustomer2>() {
//            @Override
//            public Predicate toPredicate(Root<MyCustomer2> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder cb) {
//                CriteriaQuery<MyCustomer2> q1 = cb.createQuery(MyCustomer2.class);
//                Join<LinkMan, MyCustomer2> myOrderJoin = root.join("set");
//                q1.select(myOrderJoin)
//                        .where(
//                                cb.equal(root.get("custName"), "百度"),//cId=1
//                                cb.equal(root.get("set").get("lkmName"), "张三")//对象customer的firstName=Jack
//                        );
//                Predicate p1 = q1.getRestriction();
//                return p1;
//            }
//        });


    }


}
