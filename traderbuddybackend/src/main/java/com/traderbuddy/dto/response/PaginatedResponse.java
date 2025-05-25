package com.traderbuddy.dto.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class PaginatedResponse<T> {
    private List<T> data;
    private String nextCursor;
}


/*
 * @GetMapping("/paginated")
    public ResponseEntity<PaginatedResponse<Post>> getPaginatedPosts(
            @RequestParam(required = false) String cursor,
            @RequestParam(defaultValue = "10") int limit
    ) {
        LocalDateTime cursorDate = (cursor != null) ? LocalDateTime.parse(cursor) : null;
        Pageable pageable = PageRequest.of(0, limit);
        List<Post> posts = postRepository.findNextPage(cursorDate, pageable);

        String nextCursor = posts.size() < limit ? null : posts.get(posts.size() - 1).getCreatedAt().toString();

        return ResponseEntity.ok(new PaginatedResponse<>(posts, nextCursor));
    }
 */