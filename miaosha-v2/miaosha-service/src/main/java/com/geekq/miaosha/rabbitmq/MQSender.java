package com.geekq.miaosha.rabbitmq;

import com.geekq.miaosha.redis.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class MQSender {

    @Autowired
    AmqpTemplate amqpTemplate;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void sendMiaoshaMessage(MiaoshaMessage mm) {
        String msg = RedisService.beanToString(mm);
        log.info("send message:" + msg);
        amqpTemplate.convertAndSend(MQConfig.MIAOSHA_QUEUE, msg);
    }

    /**
     * 站内信
     *
     * @param mm
     */
    public void sendMessage(MiaoshaMessage mm) {
        log.info("send message:" + "11111");
        rabbitTemplate.convertAndSend(MQConfig.EXCHANGE_TOPIC, "miaosha_*", "111111111");
    }

}
