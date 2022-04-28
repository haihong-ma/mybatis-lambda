# mybatis-lambda
使用此框架，可以直接通过Lambda表达式操作数据库，使用步骤如下
1. 执行命令`git clone https://github.com/haihong-ma/mybatis-lambda`下载源码
2. 执行命令`mvn clean install`安装包到本地仓库
3. 新建spring boot项目，在pom文件中添加如下依赖
```xml
<dependency>
    <groupId>ma.haihong</groupId>
    <artifactId>mybatis-lambda-spring-boot-starter</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```
4. 定义实体（若不加注解，表名或字段名，将解析类名或属性名，通过驼峰转下划线。主键注解必须添加，否则不能使用XXXById(s)方法）
```java
@Data
@TableName("sample_table")
public class SampleDO {
    
    @TableId("id")
    private Long id;
    @TableField("name")
    private String name;
    //@TableField("age")
    private Integer age;
}
```
5. 定义Mapper接口（继承BaseMapper接口）
```java
@Mapper
public interface SampleMapper extends BaseMapper<SampleDO> {
}
```
6. 使用Mapper方法操作数据库
```java
@SpringBootTest
class DemoApplicationTests {

    @Autowired
    private SampleMapper sampleMapper;

    @Test
    void contextLoads() {
        long id = 1;
        String name = "name1";
        List<Long> ids = Arrays.asList(1L, 2L, 3L);
        List<String> names = Arrays.asList("name1", "name2");
        SampleDO sampleParam = new SampleDO() {{
            setId(1L);
            setAge(18);
            setName("John");
        }};
        SampleDO sampleParam1 = new SampleDO() {{
            setId(2L);
            setAge(28);
            setName("name1");
        }};
        SampleDO sampleParam2 = new SampleDO() {{
            setId(3L);
            setAge(38);
            setName("name2");
        }};

        //插入
        sampleMapper.insert(sampleParam);
        sampleMapper.insertList(Arrays.asList(sampleParam1,sampleParam2));
        
        //查询
        SampleDO sample = sampleMapper.findById(id);
        List<SampleDO> samples = sampleMapper.findByIds(ids);
        SampleDO sample1 = sampleMapper.lambda().where(w -> w.getId() == id).findOne();
        List<SampleDO> samples1 = sampleMapper.lambda().where(w -> names.contains(w.getName()) || w.getAge() == 18).findList();
        Map<String,Integer> map = sampleMapper.lambda().where(w -> names.contains(w.getName())).toMap(SampleDO::getName, SampleDO::getAge);

        String name1 = sampleMapper.lambda().where(w -> w.getId() == id).select(SampleDO::getName).findOne();
        List<String> names1 = sampleMapper.lambda().where(w -> ids.contains(w.getId())).select(SampleDO::getName).findList();

        //聚合查询
        int maxAge = sampleMapper.lambda().max(SampleDO::getAge);
        int minAge = sampleMapper.lambda().min(SampleDO::getAge);
        long count = sampleMapper.lambda().where(w -> names.contains(w.getName())).count();
        long distinctAgeCount = sampleMapper.lambda().where(w -> w.getName().contains("ma")).count(SampleDO::getAge, true);

        //更新
        sampleParam2.setName("test2");
        sampleMapper.updateById(sampleParam2);
        sampleMapper.lambda().update(sampleParam, w -> w.getId().equals(1L));
        sampleMapper.lambda().update(u -> u.set(SampleDO::getId, 1L).set(SampleDO::getAge, 10), w -> w.getName().equals(name));

        //删除
        sampleMapper.deleteById(id);
        sampleMapper.deleteByIds(ids);
        sampleMapper.lambda().delete(w -> ids.contains(w.getId()));
    }
}
```
