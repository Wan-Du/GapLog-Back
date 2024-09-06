package com.gaplog.server.domain.user.dto;

import com.gaplog.server.domain.user.domain.Seriousness;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SeriousnessDto {
    private Long seriousnessId;
    private Long userId;
    private int tier;
    private int seriousnessCount;

    public static SeriousnessDto from(Seriousness seriousness, int totalSeriousnessCount) {
        return new SeriousnessDto(
                seriousness.getId(),
                seriousness.getUser().getId(),
                seriousness.getTier(),
                totalSeriousnessCount
        );
    }
}
