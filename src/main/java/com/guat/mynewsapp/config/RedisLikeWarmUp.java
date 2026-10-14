package com.guat.mynewsapp.config;

import com.guat.mynewsapp.entity.PostLike;
import com.guat.mynewsapp.mapper.PostLikeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 预热
 */
@Component
public class RedisLikeWarmUp implements ApplicationRunner {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private PostLikeMapper postLikeMapper;

    private static final String LIKE_SET_PREFIX = "post:like:";
    private static final String LIKE_COUNT_PREFIX = "post:like:count:";

    @Override
    public void run(ApplicationArguments args) {
        System.out.println("========== 开始预热 Redis 点赞数据 ==========");

        // 1. 查询 MySQL 中所有有效点赞记录（is_cancel = 0）
        List<PostLike> allValidLikes = postLikeMapper.selectAllValidLikes();

        if (allValidLikes == null || allValidLikes.isEmpty()) {
            System.out.println("无历史点赞数据，预热结束");
            return;
        }

        // 2. 按帖子ID分组，批量写入 Redis
        allValidLikes.stream()
                .collect(Collectors.groupingBy(PostLike::getNewsId))
                .forEach((postId, likeList) -> {
                    String setKey = LIKE_SET_PREFIX + postId;
                    String countKey = LIKE_COUNT_PREFIX + postId;

                    // 提取该帖子所有点赞用户ID
                    Object[] userIds = likeList.stream()
                            .map(PostLike::getUserId)
                            .toArray();

                    // 批量加入 Set
                    redisTemplate.opsForSet().add(setKey, userIds);
                    // 设置点赞计数
                    redisTemplate.opsForValue().set(countKey, likeList.size());
                });

        System.out.println("========== Redis 点赞数据预热完成，共 " + allValidLikes.size() + " 条记录 ==========");
    }
}
