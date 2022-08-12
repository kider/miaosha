package com.geekq.miaosha.controller;

import com.geekq.api.base.AbstractResult;
import com.geekq.api.base.Result;
import com.geekq.api.base.enums.ResultStatus;
import com.geekq.api.base.exception.GlobleException;
import com.geekq.api.pojo.Goods;
import com.geekq.api.pojo.User;
import com.geekq.api.service.GoodsService;
import com.geekq.miaosha.interceptor.RequireLogin;
import com.geekq.miasha.redis.GoodsKey;
import com.geekq.miasha.vo.GoodsDetailVo;
import org.apache.dubbo.config.annotation.DubboReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Controller
@RequestMapping("/goods")
public class GoodsController extends BaseController {

    private static Logger log = LoggerFactory.getLogger(GoodsController.class);

    @DubboReference
    private GoodsService goodsService;

    /**
     * QPS:1267 load:15 mysql
     * 5000 * 10
     * QPS:2884, load:5
     */
    @RequireLogin(seconds = 5, maxCount = 5, needLogin = true)
    @RequestMapping(value = "/list", produces = "text/html")
    public void list(HttpServletRequest request, HttpServletResponse response, Model model, User user) {
        model.addAttribute("user", user);
        Result<List<Goods>> resultGoods = goodsService.list();
        if (!AbstractResult.isSuccess(resultGoods)) {
            throw new GlobleException(ResultStatus.SYSTEM_ERROR);
        }
        List<Goods> goodsList = resultGoods.getData();
        model.addAttribute("goodsList", goodsList);
        render(request, response, model, "goods_list", GoodsKey.getGoodsList, "");
    }

    /**
     * 数据库很少使用long的　，　id 正常使一般使用　snowflake 分布式自增id
     *
     * @param model
     * @param user
     * @param goodsId
     * @return
     */
    @RequestMapping(value = "/detail/{goodsId}")
    @ResponseBody
    public Result<GoodsDetailVo> detail(HttpServletRequest request, HttpServletResponse response, Model model, User user,
                                        @PathVariable("goodsId") long goodsId) {
        Result<GoodsDetailVo> result = Result.build();

        Result<Goods> goodsVoOrderResultOrder = goodsService.getMsGoodsByGoodsId(goodsId);
        if (!AbstractResult.isSuccess(goodsVoOrderResultOrder)) {
            throw new GlobleException(ResultStatus.SESSION_ERROR);
        }

        Goods goods = goodsVoOrderResultOrder.getData();
        long startAt = goods.getStartDate().getTime();
        long endAt = goods.getEndDate().getTime();
        long now = System.currentTimeMillis();
        int miaoshaStatus = 0;
        int remainSeconds = 0;
        if (now < startAt) {//秒杀还没开始，倒计时
            miaoshaStatus = 0;
            remainSeconds = (int) ((startAt - now) / 1000);
        } else if (now > endAt) {//秒杀已经结束
            miaoshaStatus = 2;
            remainSeconds = -1;
        } else {//秒杀进行中
            miaoshaStatus = 1;
            remainSeconds = 0;
        }
        GoodsDetailVo vo = new GoodsDetailVo();
        vo.setGoods(goods);
        vo.setUser(user);
        vo.setRemainSeconds(remainSeconds);
        vo.setMiaoshaStatus(miaoshaStatus);
        result.setData(vo);
        return result;
    }

}
