/**
 * @ClasssName JSONAttributeConverter
 * @Author Vince
 * @Date 2020-6-1 14:55
 * @Version 1.0
 **/
public interface JSONAttributeConverter<T extends Object> extends AttributeConverter<T, String> {

    String CLASS_NAME = JSONAttributeConverter.class.getName();

    default String convertToDatabaseColumn(T entity) {
        try {
            if (entity == null) return "{}";
            return JSONObject.toJSONString(entity);
        } catch (Exception e) {
            throw new AppException(e.getMessage(), e);
        }
    }

    default T convertToEntityAttribute(String json) {
        try {
            Type[] interfaces = this.getClass().getGenericInterfaces();
            Optional<Type> converterType = Stream.of(interfaces)
                    .filter(item -> item instanceof ParameterizedType)
                    .filter(item -> CLASS_NAME.equals(((ParameterizedType) item).getRawType().getTypeName()))
                    .findFirst();
            if (!converterType.isPresent()) throw new AppException("错误的JSONAttributeConverter实现类");
            Type[] types = ((ParameterizedType) converterType.get()).getActualTypeArguments();
            if (Objects.isNull(types)) throw new AppException("错误的JSONAttributeConverter实现类");
            Class tClass = (Class) types[0];
            if (Strings.isNullOrEmpty(json)) return (T) JSONObject.parseObject("{}", tClass);
            return (T) JSONObject.parseObject(json, tClass);
        } catch (Exception e) {
            throw new AppException("不识别的格式化类型", e);
        }
    }
}
