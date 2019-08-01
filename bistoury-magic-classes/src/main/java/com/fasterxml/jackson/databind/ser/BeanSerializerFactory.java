package com.fasterxml.jackson.databind.ser;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerator;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;

import com.fasterxml.jackson.annotation.SimpleObjectIdResolver;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.cfg.ConfigOverride;
import com.fasterxml.jackson.databind.cfg.SerializerFactoryConfig;
import com.fasterxml.jackson.databind.introspect.*;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.fasterxml.jackson.databind.jsontype.TypeResolverBuilder;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.ser.impl.FilteredBeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.impl.ObjectIdWriter;
import com.fasterxml.jackson.databind.ser.impl.PropertyBasedObjectIdGenerator;
import com.fasterxml.jackson.databind.ser.std.AtomicReferenceSerializer;
import com.fasterxml.jackson.databind.ser.std.MapSerializer;
import com.fasterxml.jackson.databind.ser.std.StdDelegatingSerializer;
import com.fasterxml.jackson.databind.type.ReferenceType;
import com.fasterxml.jackson.databind.util.ClassUtil;
import com.fasterxml.jackson.databind.util.Converter;
import qunar.tc.bistoury.magic.classes.MagicUtils;

/**
 * 类描述详见jackson，copy过来然后修改了一些代码，具体修改见123行。
 * json序列化时替换为此类会防止循环引用爆栈，
 * 不这样做的话jackson只有在序列化添加了相应注解的类才会检测循环引用并防止爆栈，
 * 但是我们有序列化任意类的场景，在这种场景下需要使用这个类。
 * fastjson使用getter方法来序列化，getter方法可能对对象数据有修改；gson和jackson相似，也需要在类上面加注解。
 * @author zhenyu.nie created on 2018 2018/11/30 14:07
 */
public class BeanSerializerFactory
        extends BasicSerializerFactory
        implements java.io.Serializable // since 2.1
{
    private static final long serialVersionUID = 1;

    /**
     * Like {@link BasicSerializerFactory}, this factory is stateless, and
     * thus a single shared global (== singleton) instance can be used
     * without thread-safety issues.
     */
    public final static BeanSerializerFactory instance = new BeanSerializerFactory(null);

    /*
    /**********************************************************
    /* Life-cycle: creation, configuration
    /**********************************************************
     */

    /**
     * Constructor for creating instances with specified configuration.
     */
    protected BeanSerializerFactory(SerializerFactoryConfig config)
    {
        super(config);
    }

    /**
     * Method used by module registration functionality, to attach additional
     * serializer providers into this serializer factory. This is typically
     * handled by constructing a new instance with additional serializers,
     * to ensure thread-safe access.
     */
    @Override
    public SerializerFactory withConfig(SerializerFactoryConfig config)
    {
        if (_factoryConfig == config) {
            return this;
        }
        /* 22-Nov-2010, tatu: Handling of subtypes is tricky if we do immutable-with-copy-ctor;
         *    and we pretty much have to here either choose between losing subtype instance
         *    when registering additional serializers, or losing serializers.
         *    Instead, let's actually just throw an error if this method is called when subtype
         *    has not properly overridden this method; this to indicate problem as soon as possible.
         */
        if (getClass() != BeanSerializerFactory.class) {
            throw new IllegalStateException("Subtype of BeanSerializerFactory ("+getClass().getName()
                    +") has not properly overridden method 'withAdditionalSerializers': can not instantiate subtype with "
                    +"additional serializer definitions");
        }
        return new BeanSerializerFactory(config);
    }

    @Override
    protected Iterable<Serializers> customSerializers() {
        return _factoryConfig.serializers();
    }

    /*
    /**********************************************************
    /* SerializerFactory impl
    /**********************************************************
     */

    /**
     * Main serializer constructor method. We will have to be careful
     * with respect to ordering of various method calls: essentially
     * we want to reliably figure out which classes are standard types,
     * and which are beans. The problem is that some bean Classes may
     * implement standard interfaces (say, {@link java.lang.Iterable}.
     *<p>
     * Note: sub-classes may choose to complete replace implementation,
     * if they want to alter priority of serializer lookups.
     */
    @Override
    @SuppressWarnings("unchecked")
    public JsonSerializer<Object> createSerializer(SerializerProvider prov,
                                                   JavaType origType)
            throws JsonMappingException
    {
        // Very first thing, let's check if there is explicit serializer annotation:
        final SerializationConfig config = prov.getConfig();
        BeanDescription beanDesc = config.introspect(origType);

        // 当bean没有添加防止循环引用爆栈的注解时，如果是需要的场景，进行相应处理
        if (beanDesc.getObjectIdInfo() == null && MagicUtils.needMagic()) {
            try {
                Field objectIdInfo = BasicBeanDescription.class.getDeclaredField("_objectIdInfo");
                objectIdInfo.setAccessible(true);
                objectIdInfo.set(beanDesc, new ObjectIdInfo(new PropertyName("@qdebug_id"), Object.class, ObjectIdGenerators.IntSequenceGenerator.class, SimpleObjectIdResolver.class));
            } catch (Throwable e) {
                throw new IllegalStateException("unexpected error, object id info set fail", e);
            }
        }

        JsonSerializer<?> ser = findSerializerFromAnnotation(prov, beanDesc.getClassInfo());
        if (ser != null) {
            return (JsonSerializer<Object>) ser;
        }
        boolean staticTyping;
        // Next: we may have annotations that further indicate actual type to use (a super type)
        final AnnotationIntrospector intr = config.getAnnotationIntrospector();
        JavaType type;

        if (intr == null) {
            type = origType;
        } else {
            try {
                type = intr.refineSerializationType(config, beanDesc.getClassInfo(), origType);
            } catch (JsonMappingException e) {
                return prov.reportBadTypeDefinition(beanDesc, e.getMessage());
            }
        }
        if (type == origType) { // no changes, won't force static typing
            staticTyping = false;
        } else { // changes; assume static typing; plus, need to re-introspect if class differs
            staticTyping = true;
            if (!type.hasRawClass(origType.getRawClass())) {
                beanDesc = config.introspect(type);
            }
        }
        // Slight detour: do we have a Converter to consider?
        Converter<Object,Object> conv = beanDesc.findSerializationConverter();
        if (conv == null) { // no, simple
            return (JsonSerializer<Object>) _createSerializer2(prov, type, beanDesc, staticTyping);
        }
        JavaType delegateType = conv.getOutputType(prov.getTypeFactory());

        // One more twist, as per [databind#288]; probably need to get new BeanDesc
        if (!delegateType.hasRawClass(type.getRawClass())) {
            beanDesc = config.introspect(delegateType);
            // [#359]: explicitly check (again) for @JsonSerializer...
            ser = findSerializerFromAnnotation(prov, beanDesc.getClassInfo());
        }
        // [databind#731]: Should skip if nominally java.lang.Object
        if (ser == null && !delegateType.isJavaLangObject()) {
            ser = _createSerializer2(prov, delegateType, beanDesc, true);
        }
        return new StdDelegatingSerializer(conv, delegateType, ser);
    }

    protected JsonSerializer<?> _createSerializer2(SerializerProvider prov,
                                                   JavaType type, BeanDescription beanDesc, boolean staticTyping)
            throws JsonMappingException
    {
        JsonSerializer<?> ser = null;
        final SerializationConfig config = prov.getConfig();

        // Container types differ from non-container types
        // (note: called method checks for module-provided serializers)
        if (type.isContainerType()) {
            if (!staticTyping) {
                staticTyping = usesStaticTyping(config, beanDesc, null);
            }
            // 03-Aug-2012, tatu: As per [databind#40], may require POJO serializer...
            ser =  buildContainerSerializer(prov, type, beanDesc, staticTyping);
            // Will return right away, since called method does post-processing:
            if (ser != null) {
                return ser;
            }
        } else {
            if (type.isReferenceType()) {
                ser = findReferenceSerializer(prov, (ReferenceType) type, beanDesc, staticTyping);
            } else {
                // Modules may provide serializers of POJO types:
                for (Serializers serializers : customSerializers()) {
                    ser = serializers.findSerializer(config, type, beanDesc);
                    if (ser != null) {
                        break;
                    }
                }
            }
            // 25-Jun-2015, tatu: Then JsonSerializable, @JsonValue etc. NOTE! Prior to 2.6,
            //    this call was BEFORE custom serializer lookup, which was wrong.
            if (ser == null) {
                ser = findSerializerByAnnotations(prov, type, beanDesc);
            }
        }

        if (ser == null) {
            // Otherwise, we will check "primary types"; both marker types that
            // indicate specific handling (JsonSerializable), or main types that have
            // precedence over container types
            ser = findSerializerByLookup(type, config, beanDesc, staticTyping);
            if (ser == null) {
                ser = findSerializerByPrimaryType(prov, type, beanDesc, staticTyping);
                if (ser == null) {
                    // And this is where this class comes in: if type is not a
                    // known "primary JDK type", perhaps it's a bean? We can still
                    // get a null, if we can't find a single suitable bean property.
                    ser = findBeanSerializer(prov, type, beanDesc);
                    // Finally: maybe we can still deal with it as an implementation of some basic JDK interface?
                    if (ser == null) {
                        ser = findSerializerByAddonType(config, type, beanDesc, staticTyping);
                        // 18-Sep-2014, tatu: Actually, as per [jackson-databind#539], need to get
                        //   'unknown' serializer assigned earlier, here, so that it gets properly
                        //   post-processed
                        if (ser == null) {
                            ser = prov.getUnknownTypeSerializer(beanDesc.getBeanClass());
                        }
                    }
                }
            }
        }
        if (ser != null) {
            // [databind#120]: Allow post-processing
            if (_factoryConfig.hasSerializerModifiers()) {
                for (BeanSerializerModifier mod : _factoryConfig.serializerModifiers()) {
                    ser = mod.modifySerializer(config, beanDesc, ser);
                }
            }
        }
        return ser;
    }

    /*
    /**********************************************************
    /* Other public methods that are not part of
    /* JsonSerializerFactory API
    /**********************************************************
     */

    /**
     * Method that will try to construct a {@link BeanSerializer} for
     * given class. Returns null if no properties are found.
     */
    public JsonSerializer<Object> findBeanSerializer(SerializerProvider prov, JavaType type,
                                                     BeanDescription beanDesc)
            throws JsonMappingException
    {
        // First things first: we know some types are not beans...
        if (!isPotentialBeanType(type.getRawClass())) {
            // 03-Aug-2012, tatu: Except we do need to allow serializers for Enums,
            //   as per [databind#24]
            if (!type.isEnumType()) {
                return null;
            }
        }
        return constructBeanSerializer(prov, beanDesc);
    }

    /**
     * @since 2.7
     */
    public JsonSerializer<?> findReferenceSerializer(SerializerProvider prov, ReferenceType refType,
                                                     BeanDescription beanDesc, boolean staticTyping)
            throws JsonMappingException
    {
        JavaType contentType = refType.getContentType();
        TypeSerializer contentTypeSerializer = contentType.getTypeHandler();
        final SerializationConfig config = prov.getConfig();
        if (contentTypeSerializer == null) {
            contentTypeSerializer = createTypeSerializer(config, contentType);
        }
        JsonSerializer<Object> contentSerializer = contentType.getValueHandler();
        for (Serializers serializers : customSerializers()) {
            JsonSerializer<?> ser = serializers.findReferenceSerializer(config, refType, beanDesc,
                    contentTypeSerializer, contentSerializer);
            if (ser != null) {
                return ser;
            }
        }
        if (refType.isTypeOrSubTypeOf(AtomicReference.class)) {
            return new AtomicReferenceSerializer(refType, staticTyping,
                    contentTypeSerializer, contentSerializer);
        }
        return null;
    }

    /**
     * Method called to create a type information serializer for values of given
     * non-container property
     * if one is needed. If not needed (no polymorphic handling configured), should
     * return null.
     *
     * @param baseType Declared type to use as the base type for type information serializer
     *
     * @return Type serializer to use for property values, if one is needed; null if not.
     */
    public TypeSerializer findPropertyTypeSerializer(JavaType baseType,
                                                     SerializationConfig config, AnnotatedMember accessor)
            throws JsonMappingException
    {
        AnnotationIntrospector ai = config.getAnnotationIntrospector();
        TypeResolverBuilder<?> b = ai.findPropertyTypeResolver(config, accessor, baseType);
        TypeSerializer typeSer;

        // Defaulting: if no annotations on member, check value class
        if (b == null) {
            typeSer = createTypeSerializer(config, baseType);
        } else {
            Collection<NamedType> subtypes = config.getSubtypeResolver().collectAndResolveSubtypesByClass(
                    config, accessor, baseType);
            typeSer = b.buildTypeSerializer(config, baseType, subtypes);
        }
        return typeSer;
    }

    /**
     * Method called to create a type information serializer for values of given
     * container property
     * if one is needed. If not needed (no polymorphic handling configured), should
     * return null.
     *
     * @param containerType Declared type of the container to use as the base type for type information serializer
     *
     * @return Type serializer to use for property value contents, if one is needed; null if not.
     */
    public TypeSerializer findPropertyContentTypeSerializer(JavaType containerType,
                                                            SerializationConfig config, AnnotatedMember accessor)
            throws JsonMappingException
    {
        JavaType contentType = containerType.getContentType();
        AnnotationIntrospector ai = config.getAnnotationIntrospector();
        TypeResolverBuilder<?> b = ai.findPropertyContentTypeResolver(config, accessor, containerType);
        TypeSerializer typeSer;

        // Defaulting: if no annotations on member, check value class
        if (b == null) {
            typeSer = createTypeSerializer(config, contentType);
        } else {
            Collection<NamedType> subtypes = config.getSubtypeResolver().collectAndResolveSubtypesByClass(config,
                    accessor, contentType);
            typeSer = b.buildTypeSerializer(config, contentType, subtypes);
        }
        return typeSer;
    }

    /*
    /**********************************************************
    /* Overridable non-public factory methods
    /**********************************************************
     */

    /**
     * Method called to construct serializer for serializing specified bean type.
     *
     * @since 2.1
     */
    @SuppressWarnings("unchecked")
    protected JsonSerializer<Object> constructBeanSerializer(SerializerProvider prov,
                                                             BeanDescription beanDesc)
            throws JsonMappingException
    {
        // 13-Oct-2010, tatu: quick sanity check: never try to create bean serializer for plain Object
        // 05-Jul-2012, tatu: ... but we should be able to just return "unknown type" serializer, right?
        if (beanDesc.getBeanClass() == Object.class) {
            return prov.getUnknownTypeSerializer(Object.class);
//            throw new IllegalArgumentException("Can not create bean serializer for Object.class");
        }
        final SerializationConfig config = prov.getConfig();
        BeanSerializerBuilder builder = constructBeanSerializerBuilder(beanDesc);
        Method setConfigMethod;
        try {
            setConfigMethod = BeanSerializerBuilder.class.getDeclaredMethod("setConfig", SerializationConfig.class);
            setConfigMethod.setAccessible(true);
            setConfigMethod.invoke(builder, config);
        } catch (Throwable e) {
            throw new IllegalStateException(e);
        }

        // First: any detectable (auto-detect, annotations) properties to serialize?
        List<BeanPropertyWriter> props = findBeanProperties(prov, beanDesc, builder);
        if (props == null) {
            props = new ArrayList<BeanPropertyWriter>();
        } else {
            props = removeOverlappingTypeIds(prov, beanDesc, builder, props);
        }

        // [databind#638]: Allow injection of "virtual" properties:
        prov.getAnnotationIntrospector().findAndAddVirtualProperties(config, beanDesc.getClassInfo(), props);

        // [JACKSON-440] Need to allow modification bean properties to serialize:
        if (_factoryConfig.hasSerializerModifiers()) {
            for (BeanSerializerModifier mod : _factoryConfig.serializerModifiers()) {
                props = mod.changeProperties(config, beanDesc, props);
            }
        }

        // Any properties to suppress?
        props = filterBeanProperties(config, beanDesc, props);

        // Need to allow reordering of properties to serialize
        if (_factoryConfig.hasSerializerModifiers()) {
            for (BeanSerializerModifier mod : _factoryConfig.serializerModifiers()) {
                props = mod.orderProperties(config, beanDesc, props);
            }
        }

        /* And if Object Id is needed, some preparation for that as well: better
         * do before view handling, mostly for the custom id case which needs
         * access to a property
         */
        builder.setObjectIdWriter(constructObjectIdHandler(prov, beanDesc, props));

        builder.setProperties(props);
        builder.setFilterId(findFilterId(config, beanDesc));

        AnnotatedMember anyGetter = beanDesc.findAnyGetter();
        if (anyGetter != null) {
            JavaType type = anyGetter.getType();
            // copied from BasicSerializerFactory.buildMapSerializer():
            boolean staticTyping = config.isEnabled(MapperFeature.USE_STATIC_TYPING);
            JavaType valueType = type.getContentType();
            TypeSerializer typeSer = createTypeSerializer(config, valueType);
            // last 2 nulls; don't know key, value serializers (yet)
            // 23-Feb-2015, tatu: As per [databind#705], need to support custom serializers
            JsonSerializer<?> anySer = findSerializerFromAnnotation(prov, anyGetter);
            if (anySer == null) {
                // TODO: support '@JsonIgnoreProperties' with any setter?
                anySer = MapSerializer.construct(/* ignored props*/ (Set<String>) null,
                        type, staticTyping, typeSer, null, null, /*filterId*/ null);
            }
            // TODO: can we find full PropertyName?
            PropertyName name = PropertyName.construct(anyGetter.getName());
            BeanProperty.Std anyProp = new BeanProperty.Std(name, valueType, null,
                    beanDesc.getClassAnnotations(), anyGetter, PropertyMetadata.STD_OPTIONAL);
            builder.setAnyGetter(new AnyGetterWriter(anyProp, anyGetter, anySer));
        }
        // Next: need to gather view information, if any:
        processViews(config, builder);

        // Finally: let interested parties mess with the result bit more...
        if (_factoryConfig.hasSerializerModifiers()) {
            for (BeanSerializerModifier mod : _factoryConfig.serializerModifiers()) {
                builder = mod.updateBuilder(config, beanDesc, builder);
            }
        }

        JsonSerializer<Object> ser = (JsonSerializer<Object>) builder.build();
        if (ser == null) {
            // If we get this far, there were no properties found, so no regular BeanSerializer
            // would be constructed. But, couple of exceptions.
            // First: if there are known annotations, just create 'empty bean' serializer
            if (beanDesc.hasKnownClassAnnotations()) {
                return builder.createDummy();
            }
        }
        return ser;
    }

    protected ObjectIdWriter constructObjectIdHandler(SerializerProvider prov,
                                                      BeanDescription beanDesc, List<BeanPropertyWriter> props)
            throws JsonMappingException
    {
        ObjectIdInfo objectIdInfo = beanDesc.getObjectIdInfo();
        if (objectIdInfo == null) {
            return null;
        }
        ObjectIdGenerator<?> gen;
        Class<?> implClass = objectIdInfo.getGeneratorType();

        // Just one special case: Property-based generator is trickier
        if (implClass == ObjectIdGenerators.PropertyGenerator.class) { // most special one, needs extra work
            String propName = objectIdInfo.getPropertyName().getSimpleName();
            BeanPropertyWriter idProp = null;

            for (int i = 0, len = props.size() ;; ++i) {
                if (i == len) {
                    throw new IllegalArgumentException("Invalid Object Id definition for "+beanDesc.getBeanClass().getName()
                            +": can not find property with name '"+propName+"'");
                }
                BeanPropertyWriter prop = props.get(i);
                if (propName.equals(prop.getName())) {
                    idProp = prop;
                    /* Let's force it to be the first property to output
                     * (although it may still get rearranged etc)
                     */
                    if (i > 0) {
                        props.remove(i);
                        props.add(0, idProp);
                    }
                    break;
                }
            }
            JavaType idType = idProp.getType();
            gen = new PropertyBasedObjectIdGenerator(objectIdInfo, idProp);
            // one more thing: must ensure that ObjectIdWriter does not actually write the value:
            return ObjectIdWriter.construct(idType, (PropertyName) null, gen, objectIdInfo.getAlwaysAsId());

        }
        // other types are simpler
        JavaType type = prov.constructType(implClass);
        // Could require type to be passed explicitly, but we should be able to find it too:
        JavaType idType = prov.getTypeFactory().findTypeParameters(type, ObjectIdGenerator.class)[0];
        gen = prov.objectIdGeneratorInstance(beanDesc.getClassInfo(), objectIdInfo);
        return ObjectIdWriter.construct(idType, objectIdInfo.getPropertyName(), gen,
                objectIdInfo.getAlwaysAsId());
    }

    /**
     * Method called to construct a filtered writer, for given view
     * definitions. Default implementation constructs filter that checks
     * active view type to views property is to be included in.
     */
    protected BeanPropertyWriter constructFilteredBeanWriter(BeanPropertyWriter writer,
                                                             Class<?>[] inViews)
    {
        return FilteredBeanPropertyWriter.constructViewBased(writer, inViews);
    }

    protected PropertyBuilder constructPropertyBuilder(SerializationConfig config,
                                                       BeanDescription beanDesc)
    {
        return new PropertyBuilder(config, beanDesc);
    }

    protected BeanSerializerBuilder constructBeanSerializerBuilder(BeanDescription beanDesc) {
        return new BeanSerializerBuilder(beanDesc);
    }

    /*
    /**********************************************************
    /* Overridable non-public introspection methods
    /**********************************************************
     */

    /**
     * Helper method used to skip processing for types that we know
     * can not be (i.e. are never consider to be) beans:
     * things like primitives, Arrays, Enums, and proxy types.
     *<p>
     * Note that usually we shouldn't really be getting these sort of
     * types anyway; but better safe than sorry.
     */
    protected boolean isPotentialBeanType(Class<?> type)
    {
        return (ClassUtil.canBeABeanType(type) == null) && !ClassUtil.isProxyType(type);
    }

    /**
     * Method used to collect all actual serializable properties.
     * Can be overridden to implement custom detection schemes.
     */
    protected List<BeanPropertyWriter> findBeanProperties(SerializerProvider prov,
                                                          BeanDescription beanDesc, BeanSerializerBuilder builder)
            throws JsonMappingException
    {
        List<BeanPropertyDefinition> properties = beanDesc.findProperties();
        final SerializationConfig config = prov.getConfig();

        // ignore specified types
        removeIgnorableTypes(config, beanDesc, properties);

        // and possibly remove ones without matching mutator...
        if (config.isEnabled(MapperFeature.REQUIRE_SETTERS_FOR_GETTERS)) {
            removeSetterlessGetters(config, beanDesc, properties);
        }

        // nothing? can't proceed (caller may or may not throw an exception)
        if (properties.isEmpty()) {
            return null;
        }
        // null is for value type serializer, which we don't have access to from here (ditto for bean prop)
        boolean staticTyping = usesStaticTyping(config, beanDesc, null);
        PropertyBuilder pb = constructPropertyBuilder(config, beanDesc);

        ArrayList<BeanPropertyWriter> result = new ArrayList<BeanPropertyWriter>(properties.size());
        for (BeanPropertyDefinition property : properties) {
            final AnnotatedMember accessor = property.getAccessor();
            // Type id? Requires special handling:
            if (property.isTypeId()) {
                if (accessor != null) {
                    builder.setTypeId(accessor);
                }
                continue;
            }
            // suppress writing of back references
            AnnotationIntrospector.ReferenceProperty refType = property.findReferenceType();
            if (refType != null && refType.isBackReference()) {
                continue;
            }
            if (accessor instanceof AnnotatedMethod) {
                result.add(_constructWriter(prov, property, pb, staticTyping, (AnnotatedMethod) accessor));
            } else {
                result.add(_constructWriter(prov, property, pb, staticTyping, (AnnotatedField) accessor));
            }
        }
        return result;
    }

    /*
    /**********************************************************
    /* Overridable non-public methods for manipulating bean properties
    /**********************************************************
     */

    /**
     * Overridable method that can filter out properties. Default implementation
     * checks annotations class may have.
     */
    protected List<BeanPropertyWriter> filterBeanProperties(SerializationConfig config,
                                                            BeanDescription beanDesc, List<BeanPropertyWriter> props)
    {
        // 01-May-2016, tatu: Which base type to use here gets tricky, since
        //   it may often make most sense to use general type for overrides,
        //   but what we have here may be more specific impl type. But for now
        //   just use it as is.
        JsonIgnoreProperties.Value ignorals = config.getDefaultPropertyIgnorals(beanDesc.getBeanClass(),
                beanDesc.getClassInfo());
        if (ignorals != null) {
            Set<String> ignored = ignorals.findIgnoredForSerialization();
            if (!ignored.isEmpty()) {
                Iterator<BeanPropertyWriter> it = props.iterator();
                while (it.hasNext()) {
                    if (ignored.contains(it.next().getName())) {
                        it.remove();
                    }
                }
            }
        }
        return props;
    }

    /**
     * Method called to handle view information for constructed serializer,
     * based on bean property writers.
     *<p>
     * Note that this method is designed to be overridden by sub-classes
     * if they want to provide custom view handling. As such it is not
     * considered an internal implementation detail, and will be supported
     * as part of API going forward.
     */
    protected void processViews(SerializationConfig config, BeanSerializerBuilder builder)
    {
        // whether non-annotated fields are included by default or not is configurable
        List<BeanPropertyWriter> props = builder.getProperties();
        boolean includeByDefault = config.isEnabled(MapperFeature.DEFAULT_VIEW_INCLUSION);
        final int propCount = props.size();
        int viewsFound = 0;
        BeanPropertyWriter[] filtered = new BeanPropertyWriter[propCount];
        // Simple: view information is stored within individual writers, need to combine:
        for (int i = 0; i < propCount; ++i) {
            BeanPropertyWriter bpw = props.get(i);
            Class<?>[] views = bpw.getViews();
            if (views == null) { // no view info? include or exclude by default?
                if (includeByDefault) {
                    filtered[i] = bpw;
                }
            } else {
                ++viewsFound;
                filtered[i] = constructFilteredBeanWriter(bpw, views);
            }
        }
        // minor optimization: if no view info, include-by-default, can leave out filtering info altogether:
        if (includeByDefault && viewsFound == 0) {
            return;
        }
        builder.setFilteredProperties(filtered);
    }

    /**
     * Method that will apply by-type limitations (as per [JACKSON-429]);
     * by default this is based on {@link com.fasterxml.jackson.annotation.JsonIgnoreType}
     * annotation but can be supplied by module-provided introspectors too.
     */
    protected void removeIgnorableTypes(SerializationConfig config, BeanDescription beanDesc,
                                        List<BeanPropertyDefinition> properties)
    {
        AnnotationIntrospector intr = config.getAnnotationIntrospector();
        HashMap<Class<?>,Boolean> ignores = new HashMap<Class<?>,Boolean>();
        Iterator<BeanPropertyDefinition> it = properties.iterator();
        while (it.hasNext()) {
            BeanPropertyDefinition property = it.next();
            AnnotatedMember accessor = property.getAccessor();
            if (accessor == null) {
                it.remove();
                continue;
            }
            Class<?> type = accessor.getRawType();
            Boolean result = ignores.get(type);
            if (result == null) {
                // 21-Apr-2016, tatu: For 2.8, can specify config overrides
                ConfigOverride override = config.findConfigOverride(type);
                if (override != null) {
                    result = override.getIsIgnoredType();
                }
                if (result == null) {
                    BeanDescription desc = config.introspectClassAnnotations(type);
                    AnnotatedClass ac = desc.getClassInfo();
                    result = intr.isIgnorableType(ac);
                    // default to false, non-ignorable
                    if (result == null) {
                        result = Boolean.FALSE;
                    }
                }
                ignores.put(type, result);
            }
            // lotsa work, and yes, it is ignorable type, so:
            if (result.booleanValue()) {
                it.remove();
            }
        }
    }

    /**
     * Helper method that will remove all properties that do not have a mutator.
     */
    protected void removeSetterlessGetters(SerializationConfig config, BeanDescription beanDesc,
                                           List<BeanPropertyDefinition> properties)
    {
        Iterator<BeanPropertyDefinition> it = properties.iterator();
        while (it.hasNext()) {
            BeanPropertyDefinition property = it.next();
            // one caveat: only remove implicit properties;
            // explicitly annotated ones should remain
            if (!property.couldDeserialize() && !property.isExplicitlyIncluded()) {
                it.remove();
            }
        }
    }

    /**
     * Helper method called to ensure that we do not have "duplicate" type ids.
     * Added to resolve [databind#222]
     *
     * @since 2.6
     */
    protected List<BeanPropertyWriter> removeOverlappingTypeIds(SerializerProvider prov,
                                                                BeanDescription beanDesc, BeanSerializerBuilder builder,
                                                                List<BeanPropertyWriter> props)
    {
        for (int i = 0, end = props.size(); i < end; ++i) {
            BeanPropertyWriter bpw = props.get(i);
            TypeSerializer td = bpw.getTypeSerializer();
            if ((td == null) || (td.getTypeInclusion() != As.EXTERNAL_PROPERTY)) {
                continue;
            }
            String n = td.getPropertyName();
            PropertyName typePropName = PropertyName.construct(n);

            for (BeanPropertyWriter w2 : props) {
                if ((w2 != bpw) && w2.wouldConflictWithName(typePropName)) {
                    bpw.assignTypeSerializer(null);
                    break;
                }
            }
        }
        return props;
    }

    /*
    /**********************************************************
    /* Internal helper methods
    /**********************************************************
     */

    /**
     * Secondary helper method for constructing {@link BeanPropertyWriter} for
     * given member (field or method).
     */
    protected BeanPropertyWriter _constructWriter(SerializerProvider prov,
                                                  BeanPropertyDefinition propDef,
                                                  PropertyBuilder pb, boolean staticTyping, AnnotatedMember accessor)
            throws JsonMappingException
    {
        final PropertyName name = propDef.getFullName();
        JavaType type = accessor.getType();
        BeanProperty.Std property = new BeanProperty.Std(name, type, propDef.getWrapperName(),
                pb.getClassAnnotations(), accessor, propDef.getMetadata());

        // Does member specify a serializer? If so, let's use it.
        JsonSerializer<?> annotatedSerializer = findSerializerFromAnnotation(prov,
                accessor);
        // Unlike most other code paths, serializer produced
        // here will NOT be resolved or contextualized, unless done here, so:
        if (annotatedSerializer instanceof ResolvableSerializer) {
            ((ResolvableSerializer) annotatedSerializer).resolve(prov);
        }
        // 05-Sep-2013, tatu: should be primary property serializer so:
        annotatedSerializer = prov.handlePrimaryContextualization(annotatedSerializer, property);
        // And how about polymorphic typing? First special to cover JAXB per-field settings:
        TypeSerializer contentTypeSer = null;
        // 16-Feb-2014, cgc: contentType serializers for collection-like and map-like types
        if (type.isContainerType() || type.isReferenceType()) {
            contentTypeSer = findPropertyContentTypeSerializer(type, prov.getConfig(), accessor);
        }
        // and if not JAXB collection/array with annotations, maybe regular type info?
        TypeSerializer typeSer = findPropertyTypeSerializer(type, prov.getConfig(), accessor);
        try {
            Method buildWriterMethod = PropertyBuilder.class.getDeclaredMethod("buildWriter",
                    SerializerProvider.class,
                    BeanPropertyDefinition.class,
                    JavaType.class,
                    JsonSerializer.class,
                    TypeSerializer.class,
                    TypeSerializer.class,
                    AnnotatedMember.class,
                    boolean.class);
            buildWriterMethod.setAccessible(true);
            return (BeanPropertyWriter) buildWriterMethod.invoke(pb, prov, propDef, type, annotatedSerializer,
                    typeSer, contentTypeSer, accessor, staticTyping);
        } catch (Throwable e) {
            throw new IllegalStateException(e);
        }
    }
}
