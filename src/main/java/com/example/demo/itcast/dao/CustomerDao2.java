package com.example.demo.itcast.dao;

import com.example.demo.itcast.MyCustomer;
import com.example.demo.itcast.MyCustomer2;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

/**
 * 符合SpringDataJpa的dao层接口规范
 *      JpaRepository<操作的实体类类型，实体类中主键属性的类型>
 *          * 封装了基本CRUD操作
 *      JpaSpecificationExecutor<操作的实体类类型>
 *          * 封装了复杂查询（分页）
 */
public interface CustomerDao2 extends JpaRepository<MyCustomer2,Long> ,JpaSpecificationExecutor<MyCustomer2> {



}
