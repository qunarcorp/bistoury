/*
 * Copyright 2002-2013 the original author or authors.
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

package qunar.tc.bistoury.instrument.client.spring.el;

/**
 * Default implementation of the {@link TypeConverter} interface,
 * delegating to a core Spring {@link ConversionService}.
 *
 * @author Juergen Hoeller
 * @author Andy Clement
 * @see ConversionService
 * @since 3.0
 */
class StandardTypeConverter implements TypeConverter {

    private static ConversionService defaultConversionService;

    private final ConversionService conversionService;


    /**
     * Create a StandardTypeConverter for the default ConversionService.
     */
    public StandardTypeConverter() {
        synchronized (this) {
            if (defaultConversionService == null) {
                defaultConversionService = new DefaultConversionService();
            }
        }
        this.conversionService = defaultConversionService;
    }

    /**
     * Create a StandardTypeConverter for the given ConversionService.
     *
     * @param conversionService the ConversionService to delegate to
     */
    public StandardTypeConverter(ConversionService conversionService) {
        Assert.notNull(conversionService, "ConversionService must not be null");
        this.conversionService = conversionService;
    }


    public boolean canConvert(TypeDescriptor sourceType, TypeDescriptor targetType) {
        return this.conversionService.canConvert(sourceType, targetType);
    }

    public Object convertValue(Object value, TypeDescriptor sourceType, TypeDescriptor targetType) {
        try {
            return this.conversionService.convert(value, sourceType, targetType);
        } catch (ConversionException ex) {
            throw new SpelEvaluationException(
                    ex, SpelMessage.TYPE_CONVERSION_ERROR, sourceType.toString(), targetType.toString());
        }
    }

}
