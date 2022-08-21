package com.geekq.provider.service.impl;

import com.geekq.api.pojo.Goods;
import com.geekq.provider.mapper.GoodsMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class GoodsServiceImpl {

    @Autowired
    private GoodsMapper goodsMapper;

    public List<Goods> list() {
        return goodsMapper.list();
    }

    public Goods getMsGoodsByGoodsId(long goodsId) {
        return goodsMapper.getMsGoodsByGoodsId(goodsId);
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public int reduceStock(Goods goods) {
        return goodsMapper.reduceStock(goods);
    }

}
