package com.geekq.miaosha.controller;

import com.geekq.api.base.AbstractResult;
import com.geekq.api.base.Result;
import com.geekq.api.base.enums.ResultStatus;
import com.geekq.api.base.exception.GlobleException;
import com.geekq.api.pojo.Goods;
import com.geekq.api.pojo.Order;
import com.geekq.api.pojo.User;
import com.geekq.api.service.GoodsDubboService;
import com.geekq.api.service.OrderDubboService;
import com.geekq.miaosha.interceptor.RequireLogin;
import com.geekq.miasha.redis.OrderKey;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@Controller
@RequestMapping("/order")
public class OrderController extends BaseController {

    @DubboReference
    OrderDubboService orderService;

    @DubboReference
    GoodsDubboService goodsService;

    @RequireLogin(seconds = 5, maxCount = 5, needLogin = true)
    @RequestMapping(value = "/detail/{orderId}", produces = "text/html")
    public String info(HttpServletRequest request, HttpServletResponse response, Model model, User user,
                       @PathVariable("orderId") long orderId) {
        final String redisKey = "" + orderId;
        //取缓存
        if (getCachePage(response, OrderKey.getOrderDetail, redisKey)) {
            return null;
        }
        Result<Order> orderResult = orderService.getOrderById(orderId);
        if (!AbstractResult.isSuccess(orderResult)) {
            throw new GlobleException(ResultStatus.ORDER_NOT_EXIST);
        }
        Order order = orderResult.getData();
        long goodsId = order.getGoodsId();
        Result<Goods> goodsResult = goodsService.getMsGoodsByGoodsId(goodsId);
        if (!AbstractResult.isSuccess(goodsResult)) {
            throw new GlobleException(ResultStatus.GOODS_GET_FAIL);
        }
        model.addAttribute("goods", goodsResult.getData());
        model.addAttribute("orderInfo", order);
        return render(request, response, model, "order_detail", OrderKey.getOrderDetail, "" + orderId);
    }

}
