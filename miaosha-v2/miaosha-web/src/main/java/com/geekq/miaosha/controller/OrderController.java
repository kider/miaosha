package com.geekq.miaosha.controller;

import com.geekq.api.base.AbstractResult;
import com.geekq.api.base.Result;
import com.geekq.api.pojo.Goods;
import com.geekq.api.pojo.Order;
import com.geekq.api.pojo.User;
import com.geekq.api.service.GoodsService;
import com.geekq.api.service.OrderService;
import com.geekq.api.service.UserService;
import com.geekq.miaosha.redis.RedisService;
import com.geekq.miasha.vo.OrderDetailVo;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import static com.geekq.api.base.enums.ResultStatus.ORDER_NOT_EXIST;
import static com.geekq.api.base.enums.ResultStatus.SESSION_ERROR;


@Controller
@RequestMapping("/order")
public class OrderController {

    @DubboReference
    UserService userService;

    @Autowired
    RedisService redisService;

    @DubboReference
    OrderService orderService;

    @DubboReference
    GoodsService goodsService;

    @RequestMapping("/detail")
    @ResponseBody
    public Result<OrderDetailVo> info(Model model, User user,
                                      @RequestParam("orderId") long orderId) {
        Result<OrderDetailVo> result = Result.build();
        if (user == null) {
            result.withError(SESSION_ERROR.getCode(), SESSION_ERROR.getMessage());
            return result;
        }
        Result<Order> orderResult = orderService.getOrderById(orderId);
        if (!AbstractResult.isSuccess(orderResult)) {
            result.withError(ORDER_NOT_EXIST.getCode(), ORDER_NOT_EXIST.getMessage());
            return result;
        }
        Order order = orderResult.getData();
        OrderDetailVo vo = new OrderDetailVo();
        vo.setOrder(order);
        long goodsId = order.getGoodsId();
        Result<Goods> goodsResult = goodsService.getMsGoodsByGoodsId(goodsId);
        if (AbstractResult.isSuccess(goodsResult)) {
            vo.setGoods(goodsResult.getData());
        }
        result.setData(vo);
        return result;
    }

}
