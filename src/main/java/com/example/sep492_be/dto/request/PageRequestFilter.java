package com.example.sep492_be.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageRequestFilter {
    public static final String SYMBOL_ASC = "+";
    public static final String SYMBOL_DESC = "-";
    public static final String DEFAULT_SORT = "createdAt";
    private Integer pageIndex = 1;
    private Integer pageSize = 250;
    private List<String> sort = new ArrayList<>();

    public static Pageable converToPageable(PageRequestFilter pageRequest) {
        List<Sort.Order> orders = new ArrayList<>();
        if (!CollectionUtils.isEmpty(pageRequest.getSort())) {
            String sortStr = pageRequest.getSort().get(pageRequest.getSort().size() - 1);
            if (StringUtils.hasLength(sortStr)) {
                if (sortStr.startsWith(PageRequestFilter.SYMBOL_DESC) && sortStr.length() > 1) {
                    orders.add(Sort.Order.desc(sortStr.substring(1)));
                } else if (sortStr.startsWith(PageRequestFilter.SYMBOL_ASC) && sortStr.length() > 1) {
                    orders.add(Sort.Order.asc(sortStr.substring(1)));
                } else {
                    orders.add(Sort.Order.asc(sortStr));
                }
            }
        }else{
            /** CuongVM7 20220822 Bổ sung thêm tuỳ chọn nếu không chọn sort thì sẽ mặc định lấy theo ngày update */
            orders.add(Sort.Order.desc(PageRequestFilter.DEFAULT_SORT));
        }
        return PageRequest.of(pageRequest.getPageIndex() - 1, pageRequest.getPageSize(), Sort.by(orders));
    }
}
