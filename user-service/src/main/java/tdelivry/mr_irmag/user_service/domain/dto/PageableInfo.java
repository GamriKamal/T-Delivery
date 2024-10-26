package tdelivry.mr_irmag.user_service.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Sort;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PageableInfo {
    private int pageNumber;
    private int pageSize;
    private SortInfo sort;
    private int offset;
    private boolean paged;
    private boolean unpaged;
}
