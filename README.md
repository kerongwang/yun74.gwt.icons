# yun74.gwt.icons
> 项目意图提供在GWT中方便处理Icon资源的工具

* SVG webfont process: 扫描项目中的svg文件，收集svg文件的view和path数据。提供需要使用该SVG作为Icon的应用用文件名称生成Widget。这种实现有两个好处，一是弥补GWT对SVG处理的支持不足，另一个是svg文件可以按需组织在各个代码模块里。
* mid webfont with class: 提供采用`<span class='mdi-classname'/>`方式使用webfont作为Icon。

## Working with the apache maven registry发布到github
- 准备好Github的Personal Access Token，新建或者更新~/.m2/settings.xml文件，加入以下内容：[Read More](https://docs.github.com/en/packages/working-with-a-github-packages-registry/working-with-the-apache-maven-registry)
    ```xml
    <settings>
        <servers> 
            <server>
                <id>github</id>
                <username>kerongwang</username>
                <password>YOUR GitHub OAUTH-TOKEN</password>
            </server>
        </servers>
    </settings>
    ```
- 构建并发布
    ```maven
    mvn clean deploy -s {PATH TO}/settings.xml
    ```

## 应用方式
- 在client的pom.xml文件中
    ```xml
    <dependency>
        <groupId>yun74.gwt</groupId>
        <artifactId>yun74.gwt.icons</artifactId>
        <version>1.0-SNAPSHOT</version>
        <type>gwt-lib</type>
    </dependency>
    ```

- 在client的module.gwt.xml文件中
    ```
    <inherits name="yun74.gwt.GWTIcons" />
    ```

- Java代码中的使用例子
    ```java
    import yun74.gwt.icons.Yun74Icons;
    Yun74Icons.dump((n, w) -> getView().append(n, w));
    iconContainer.appendChild(Yun74Icons.getElement(menu.iconName));
    ```