package com.geekq.miaosha.controller;

import com.geekq.api.entity.GoodsVoOrder;
import com.geekq.api.service.GoodsService;
import com.geekq.api.utils.ResultGeekQOrder;
import com.geekq.miaosha.redis.RedisService;
import com.geekq.miaosha.service.MiaoShaUserService;
import com.geekq.miaosha.service.OrderService;
import com.geekq.miasha.entity.MiaoshaUser;
import com.geekq.miasha.entity.OrderInfo;
import com.geekq.miasha.enums.resultbean.ResultGeekQ;
import com.geekq.miasha.vo.OrderDetailVo;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import static com.geekq.miasha.enums.enums.ResultStatus.ORDER_NOT_EXIST;
import static com.geekq.miasha.enums.enums.ResultStatus.SESSION_ERROR;


@Controller
@RequestMapping("/order")
public class OrderController {

    @Autowired
    MiaoShaUserService userService;

    @Autowired
    RedisService redisService;

    @Autowired
    OrderService orderService;

    @DubboReference
    GoodsService goodsService;

    @RequestMapping("/detail")
    @ResponseBody
    public ResultGeekQ<OrderDetailVo> info(Model model, MiaoshaUser user,
                                           @RequestParam("orderId") long orderId) {
        ResultGeekQ<OrderDetailVo> result = ResultGeekQ.build();
        if (user == null) {
            result.withError(SESSION_ERROR.getCode(), SESSION_ERROR.getMessage());
            return result;
        }
        OrderInfo order = orderService.getOrderById(orderId);
        if (order == null) {
            result.withError(ORDER_NOT_EXIST.getCode(), ORDER_NOT_EXIST.getMessage());
            return result;
        }
        long goodsId = order.getGoodsId();
        ResultGeekQOrder<GoodsVoOrder> resultGeekQOrder = goodsService.getGoodsVoByGoodsId(goodsId);
        OrderDetailVo vo = new OrderDetailVo();
        vo.setOrder(order);
        vo.setGoods(resultGeekQOrder.getData());
        result.setData(vo);
        return result;
    }

}
