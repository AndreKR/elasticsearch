/*
 * Licensed to Elasticsearch under one or more contributor
 * license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright
 * ownership. Elasticsearch licenses this file to you under
 * the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.elasticsearch.search.aggregations.transformer.derivative;

import org.elasticsearch.common.Nullable;
import org.elasticsearch.search.aggregations.Aggregator;
import org.elasticsearch.search.aggregations.AggregatorFactories;
import org.elasticsearch.search.aggregations.AggregatorFactory;
import org.elasticsearch.search.aggregations.InternalAggregation;
import org.elasticsearch.search.aggregations.InternalAggregations;
import org.elasticsearch.search.aggregations.bucket.histogram.InternalHistogram;
import org.elasticsearch.search.aggregations.support.AggregationContext;
import org.elasticsearch.search.aggregations.support.format.ValueFormatter;
import org.elasticsearch.search.aggregations.transformer.Transformer;

import java.util.Map;

public class DerivativeTransformer extends Transformer {

    private boolean keyed;
    private @Nullable
    ValueFormatter formatter;

    protected DerivativeTransformer(String name, boolean keyed, @Nullable ValueFormatter formatter, AggregatorFactories factories,
            AggregationContext aggregationContext, Aggregator parent,
            Map<String, Object> metaData) {
        super(name, factories, aggregationContext, parent, metaData);
        this.keyed = keyed;
        this.formatter = formatter;
    }

    @Override
    protected InternalAggregation buildAggregation(String name, int bucketDocCount, InternalAggregations bucketAggregations) {
        return new InternalDerivative<InternalHistogram.Bucket>(name, keyed, formatter, bucketAggregations, getMetaData());
    }

    @Override
    public InternalAggregation buildEmptyAggregation() {
        return new InternalDerivative<InternalHistogram.Bucket>(name, keyed, formatter, InternalAggregations.EMPTY, getMetaData());
    }

    public static class Factory extends AggregatorFactory {

        private boolean keyed;
        private ValueFormatter formatter;

        public Factory(String name, boolean keyed, @Nullable ValueFormatter formatter) {
            super(name, InternalDerivative.TYPE.name());
            this.keyed = keyed;
            this.formatter = formatter;
        }

        @Override
        protected Aggregator createInternal(AggregationContext context, Aggregator parent, long expectedBucketsCount,
                Map<String, Object> metaData) {
            return new DerivativeTransformer(name, keyed, formatter, factories, context, parent, metaData);
        }

    }

}
