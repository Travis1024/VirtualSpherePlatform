package org.travis.center.support.filter;

import com.alibaba.fastjson2.filter.SimplePropertyPreFilter;

/**
 * @ClassName PropertyPreExcludeFilter
 * @Description PropertyPreExcludeFilter
 * @Author travis-wei
 * @Version v1.0
 * @Data 2024/5/30
 */
public class PropertyPreExcludeFilter extends SimplePropertyPreFilter {
    public PropertyPreExcludeFilter addExcludes(String... filters) {
        for (String filter : filters) {
            this.getExcludes().add(filter);
        }
        return this;
    }
}
