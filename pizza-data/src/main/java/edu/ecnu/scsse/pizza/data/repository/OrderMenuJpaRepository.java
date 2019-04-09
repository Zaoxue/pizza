package edu.ecnu.scsse.pizza.data.repository;

import edu.ecnu.scsse.pizza.data.domain.OrderMenuEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

public interface OrderMenuJpaRepository extends JpaRepository<OrderMenuEntity,Integer> {
    //List<OrderMenuEntity> findByOrderId(int orderId);
    List<OrderMenuEntity> findByOrderId(Integer orderPk);
    List<OrderMenuEntity> findByOrderIdIn(List<Integer> orderPks);

    Optional<OrderMenuEntity> findByOrderIdAndMenuId(Integer orderPk, Integer menuId);

    @Transactional
    @Modifying
    @Query(value = "replace into order_menu set count=?1, order_id=?2, menu_id=?3", nativeQuery = true)
    int updateCount(Integer count, Integer orderPk, Integer menuId);

    @Query(value = "select temp.menuId,menu.name,menu.image,menu.description,menu.price,menu.state,menu.tag,temp.count\n" +
            "from\n" +
            "(select pizza_order.id,order_menu.menu_id as menuId,order_menu.count\n" +
            "from pizza_order left join order_menu \n" +
            "on pizza_order.id = order_menu.order_id\n" +
            "where pizza_order.id = ?1) as temp\n" +
            "left join menu\n" +
            "on temp.menuId = menu.id",nativeQuery = true)
    List<Object[]> findMenuListByOrderId(int orderId);
}