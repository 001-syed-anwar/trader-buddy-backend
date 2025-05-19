package com.traderbuddy.dto.response;

import java.util.List;

import com.traderbuddy.models.Channel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetChannelsResponse {
	List<Channel> channels;
}
