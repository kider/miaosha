package com.geekq.provider.mapper;

import com.geekq.api.pojo.Goods;
import org.apache.ibatis.annotations.Param;

import java.util.List;


public interface GoodsMapper {

    List<Goods> list();

    Goods getMsGoodsByGoodsId(@Param("goodsId") long goodsId);

    int reduceStock(Goods goods);

}
