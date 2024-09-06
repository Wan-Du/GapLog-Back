package com.gaplog.server.domain.user.application;
import com.gaplog.server.domain.post.dao.PostRepository;
import com.gaplog.server.domain.post.domain.Post;
import com.gaplog.server.domain.user.dao.SeriousnessRepository;
import com.gaplog.server.domain.user.dao.UserRepository;
import com.gaplog.server.domain.user.domain.Seriousness;
import com.gaplog.server.domain.user.dto.SeriousnessDto;
import com.gaplog.server.domain.user.dto.response.SeriousnessFieldResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SeriousnessService {
    private final SeriousnessRepository seriousnessRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Transactional
    public void updateSeriousnessTier(Long userId){
        // 유저의 총 진지버튼 수
        int totalSeriousnessCount = postRepository.findById(userId).stream().mapToInt(Post::getSeriousnessCount).sum();
        // 진지 티어 계산
        Seriousness seriousness = seriousnessRepository.findByUserId(userId)
                .orElseGet(()-> new Seriousness(userRepository.findById(userId).orElseThrow()));

        seriousness.updateTier(totalSeriousnessCount);
        seriousnessRepository.save(seriousness);

    }


    @Transactional(readOnly = true)
    public List<SeriousnessFieldResponse> getSeriousnessField(Long userId) {
        // 유저가 작성한 포스트와 해당 포스트의 진지 버튼 수를 날짜별로 반환
        List<Post> posts = postRepository.findByUserId(userId);

        return posts.stream()
                .collect(Collectors.groupingBy(post -> post.getCreatedAt().toLocalDate())) // 포스트 작성일 기준 그룹화
                .entrySet().stream()
                .map(entry -> {
                    LocalDate date = entry.getKey();
                    List<Post> postsOnDate = entry.getValue();
                    // 해당 날짜에 작성된 모든 포스트가 받은 진지 버튼 수를 합산
                    int seriousnessCount = postsOnDate.stream().mapToInt(Post::getSeriousnessCount).sum();
                    int postCount = postsOnDate.size();
                    return new SeriousnessFieldResponse(date, seriousnessCount, postCount);
                })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public SeriousnessDto getSeriousness(Long userId) {
        Seriousness seriousness = seriousnessRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user ID: " + userId));

        int totalSeriousnessCount = postRepository.findByUserId(userId)
                .stream()
                .mapToInt(Post::getSeriousnessCount)
                .sum();

        return SeriousnessDto.from(seriousness, totalSeriousnessCount);
    }

}
