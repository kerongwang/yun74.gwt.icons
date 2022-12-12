# yun74.gwt.icons
> 项目意图提供在GWT中方便处理Icon资源的工具

* SVG webfont process: 扫描项目中的svg文件，收集svg文件的view和path数据。提供需要使用该SVG作为Icon的应用用文件名称生成Widget。这种实现有两个好处，一是弥补GWT对SVG处理的支持不足，另一个是svg文件可以按需组织在各个代码模块里。
