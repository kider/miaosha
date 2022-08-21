package com.geekq.miaosha.controller;

import com.geekq.api.base.AbstractResult;
import com.geekq.api.base.Result;
import com.geekq.api.base.enums.ResultStatus;
import com.geekq.api.base.exception.GlobleException;
import com.geekq.api.pojo.Goods;
import com.geekq.api.pojo.User;
import com.geekq.api.service.GoodsDubboService;
import com.geekq.miaosha.interceptor.RequireLogin;
import com.geekq.miasha.redis.GoodsKey;
import com.geekq.miasha.vo.GoodsDetailVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/goods")
public class GoodsController extends BaseController {

    @DubboReference
    private GoodsDubboService goodsService;

    /**
     * 秒杀商品列表
     *
     * @param request
     * @param response
     * @param model
     * @param user
     * @author chenh
     * @date 2022/8/15 10:59
     **/
    @RequireLogin(seconds = 5, maxCount = 5, needLogin = true)
    @RequestMapping(value = "/list", produces = "text/html")
    public String list(HttpServletRequest request, HttpServletResponse response, Model model, User user) {
        Result<List<Goods>> resultGoods = goodsService.list();
        if (!AbstractResult.isSuccess(resultGoods)) {
            throw new GlobleException(ResultStatus.SYSTEM_ERROR);
        }
        List<Goods> goodsList = resultGoods.getData();
        model.addAttribute("goodsList", goodsList);
        model.addAttribute("user", user);
        return render(request, response, model, "goods_list", GoodsKey.getGoodsList, "goodsList");
    }

    /**
     * 秒杀商品详情（缓存）
     *
     * @param request
     * @param response
     * @param model
     * @param user
     * @param goodsId
     * @return {@link String}
     * @author chenh
     * @date 2022/8/15 13:38
     **/
    @RequireLogin(seconds = 5, maxCount = 5, needLogin = true)
    @RequestMapping(value = "/detail/{goodsId}", produces = "text/html")
    public String detail2(HttpServletRequest request, HttpServletResponse response, Model model, User user,
                          @PathVariable("goodsId") long goodsId) {
        final String redisKey = "goodsDetail";
        model.addAttribute("user", user);
        //取缓存
        String html = redisService.get(GoodsKey.getGoodsDetail, redisKey + goodsId, String.class);
        if (!StringUtils.isBlank(html)) {
            return html;
        }
        Result<Goods> goodsResult = goodsService.getMsGoodsByGoodsId(goodsId);
        if (!AbstractResult.isSuccess(goodsResult)) {
            throw new GlobleException(ResultStatus.SYSTEM_ERROR);
        }
        Goods goods = goodsResult.getData();
        model.addAttribute("goods", goods);

        long startAt = goods.getStartDate().getTime();
        long endAt = goods.getEndDate().getTime();
        long now = System.currentTimeMillis();

        int miaoshaStatus = 0;
        int remainSeconds = 0;
        //秒杀还没开始，倒计时
        if (now < startAt) {
            miaoshaStatus = 0;
            remainSeconds = (int) ((startAt - now) / 1000);
        }
        //秒杀已经结束
        else if (now > endAt) {
            miaoshaStatus = 2;
            remainSeconds = -1;
        }
        //秒杀进行中
        else {
            miaoshaStatus = 1;
            remainSeconds = 0;
        }
        model.addAttribute("miaoshaStatus", miaoshaStatus);
        model.addAttribute("remainSeconds", remainSeconds);

        return render(request, response, model, "goods_detail", GoodsKey.getGoodsDetail, redisKey + goodsId);
    }

    /**
     * 秒杀商品详情
     *
     * @param request
     * @param response
     * @param model
     * @param user
     * @param goodsId  商品ID
     * @return {@link Result< GoodsDetailVo>}
     * @author chenh
     * @date 2022/8/15 11:21
     **/
    @RequireLogin(seconds = 5, maxCount = 5, needLogin = true)
    @RequestMapping(value = "/detail2/{goodsId}")
    @ResponseBody
    public Result<GoodsDetailVo> detail(HttpServletRequest request, HttpServletResponse response, Model model, User user,
                                        @PathVariable("goodsId") long goodsId) {
        Result<GoodsDetailVo> result = Result.build();
        Result<Goods> goodsResult = goodsService.getMsGoodsByGoodsId(goodsId);
        if (!AbstractResult.isSuccess(goodsResult)) {
            throw new GlobleException(ResultStatus.SYSTEM_ERROR);
        }
        Goods goods = goodsResult.getData();
        long startAt = goods.getStartDate().getTime();
        long endAt = goods.getEndDate().getTime();
        long now = System.currentTimeMillis();
        int miaoshaStatus = 0;
        int remainSeconds = 0;
        //秒杀还没开始，倒计时
        if (now < startAt) {
            miaoshaStatus = 0;
            remainSeconds = (int) ((startAt - now) / 1000);
        }
        //秒杀已经结束
        else if (now > endAt) {
            miaoshaStatus = 2;
            remainSeconds = -1;
        } else {
            //秒杀进行中
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
