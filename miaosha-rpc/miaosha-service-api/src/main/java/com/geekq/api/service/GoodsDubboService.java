package com.geekq.api.service;

import com.geekq.api.base.Result;
import com.geekq.api.pojo.Goods;

import java.util.List;

public interface GoodsDubboService {

    Result<List<Goods>> list();

    Result<Goods> getMsGoodsByGoodsId(long goodsId);

    Result<Boolean> reduceStock(Goods goods);

}
