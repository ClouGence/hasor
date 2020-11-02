/*
 * Copyright 2002-2005 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.hasor.db.jdbc.lambda.segment;
import net.hasor.utils.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 提供多个 Segment 汇聚成为一个的工具。
 * @version : 2020-10-31
 * @author 赵永春 (zyc@hasor.net)
 */
public class MergeSqlSegment implements Segment {
    private final List<Segment> segments = new ArrayList<>();

    public MergeSqlSegment(List<Segment> segments) {
        this.segments.addAll(segments);
    }

    public MergeSqlSegment(Segment... segments) {
        this.segments.addAll(Arrays.asList(segments));
    }

    public void addSegment(Segment segment) {
        if (segment != null) {
            this.segments.add(segment);
        }
    }

    @Override
    public String getSqlSegment() {
        return this.getSqlSegment(this.segments);
    }

    public MergeSqlSegment sub(int form) {
        return new MergeSqlSegment(this.segments.subList(form, this.segments.size()));
    }

    public String noFirstSqlSegment() {
        return this.getSqlSegment(this.segments.subList(1, this.segments.size()));
    }

    private String getSqlSegment(List<Segment> dataList) {
        return dataList.stream().map(Segment::getSqlSegment).reduce((s1, s2) -> {
            if (StringUtils.isBlank(s1) || StringUtils.isBlank(s2)) {
                return StringUtils.isNotBlank(s1) ? s1 : s2;
            }
            return s1 + " " + s2;
        }).orElse("");
    }

    public boolean isEmpty() {
        return this.segments.isEmpty();
    }
}