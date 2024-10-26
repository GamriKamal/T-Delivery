package tdelivry.mr_irmag.user_service.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SortInfo {
    private boolean sorted;
    private boolean unsorted;
    private boolean empty;
}
