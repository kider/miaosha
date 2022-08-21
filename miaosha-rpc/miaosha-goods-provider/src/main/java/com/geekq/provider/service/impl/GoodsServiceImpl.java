package com.geekq.provider.service.impl;

import com.geekq.api.base.Result;
import com.geekq.api.base.enums.ResultStatus;
import com.geekq.api.pojo.Goods;
import com.geekq.api.service.GoodsService;
import com.geekq.provider.mapper.GoodsMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@DubboService
public class GoodsServiceImpl implements GoodsService {

    @Autowired
    GoodsMapper goodsMapper;

    @Override
    public Result<List<Goods>> list() {
        Result<List<Goods>> result = Result.build();
        try {
            result.setData(goodsMapper.list());
        } catch (Exception e) {
            log.error("获取订单数据失败！", e);
            result.withError(ResultStatus.ORDER_GET_FAIL);
        }
        return result;
    }

    @Override
    public Result<Goods> getMsGoodsByGoodsId(long goodsId) {
        Result<Goods> result = Result.build();
        try {
            result.setData(goodsMapper.getMsGoodsByGoodsId(goodsId));
        } catch (Exception e) {
            log.error("获取单个订单失败！", e);
            result.withError(ResultStatus.ORDER_GET_FAIL);
        }
        return result;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Result<Boolean> reduceStock(Goods goods) {
        int ret = goodsMapper.reduceStock(goods);
        log.info("goodsId:" + goods.getGoodsId() + "reduceStock：" + ret);
        return Result.build(ret > 0);
    }

}
