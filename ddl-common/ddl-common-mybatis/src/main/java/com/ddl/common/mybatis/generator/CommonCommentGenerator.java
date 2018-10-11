package com.ddl.common.mybatis.generator;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.Field;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.Parameter;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.internal.DefaultCommentGenerator;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * 基于中文注释的代码生成器
 */
public class CommonCommentGenerator extends DefaultCommentGenerator {

    public void addGeneralMethodComment(Method method, IntrospectedTable introspectedTable) {
        StringBuilder sb = new StringBuilder();
        method.addJavaDocLine("/**"); //$NON-NLS-1$
        sb.append(" * ");
        if (method.isConstructor()) {
            sb.append(" 构造查询条件");
        }
        String methodName = method.getName();
        if ("setOrderByClause".equals(methodName)) {
            sb.append(" 设置排序字段");
        } else if ("setDistinct".equals(methodName)) {
            sb.append(" 设置过滤重复数据");
        } else if ("getOredCriteria".equals(methodName)) {
            sb.append(" 获取当前的查询条件实例");
        } else if ("isDistinct".equals(methodName)) {
            sb.append(" 是否过滤重复数据");
        } else if ("getOrderByClause".equals(methodName)) {
            sb.append(" 获取排序字段");
        } else if ("createCriteria".equals(methodName)) {
            sb.append(" 创建一个查询条件");
        } else if ("createCriteriaInternal".equals(methodName)) {
            sb.append(" 内部构建查询条件对象");
        } else if ("clear".equals(methodName)) {
            sb.append(" 清除查询条件");
        } else if ("countByExample".equals(methodName)) {
            sb.append(" 根据指定的条件获取数据库记录数");
        } else if ("deleteByExample".equals(methodName)) {
            sb.append(" 根据指定的条件删除数据库符合条件的记录");
        } else if ("deleteByPrimaryKey".equals(methodName)) {
            sb.append(" 根据主键删除数据库的记录");
        } else if ("insert".equals(methodName)) {
            sb.append(" 新写入数据库记录");
        } else if ("insertSelective".equals(methodName)) {
            sb.append(" 动态字段,写入数据库记录");
        } else if ("selectByExample".equals(methodName)) {
            sb.append(" 根据指定的条件查询符合条件的数据库记录");
        } else if ("selectByPrimaryKey".equals(methodName)) {
            sb.append(" 根据指定主键获取一条数据库记录");
        } else if ("updateByExampleSelective".equals(methodName)) {
            sb.append(" 动态根据指定的条件来更新符合条件的数据库记录");
        } else if ("updateByExample".equals(methodName)) {
            sb.append(" 根据指定的条件来更新符合条件的数据库记录");
        } else if ("updateByPrimaryKeySelective".equals(methodName)) {
            sb.append(" 动态字段,根据主键来更新符合条件的数据库记录");
        } else if ("updateByPrimaryKey".equals(methodName)) {
            sb.append(" 根据主键来更新符合条件的数据库记录");
        }
        sb.append(",");
        sb.append(introspectedTable.getFullyQualifiedTable());
        method.addJavaDocLine(sb.toString());

        final List<Parameter> parameterList = method.getParameters();
        if (!parameterList.isEmpty()) {
            method.addJavaDocLine(" *");
            if ("or".equals(methodName)) {
                sb.append(" 增加或者的查询条件,用于构建或者查询");
            }
        } else {
            if ("or".equals(methodName)) {
                sb.append(" 创建一个新的或者查询条件");
            }
        }
        String paramterName;
        for (Parameter parameter : parameterList) {
            sb.setLength(0);
            sb.append(" * @param "); //$NON-NLS-1$
            paramterName = parameter.getName();
            sb.append(paramterName);
            if ("orderByClause".equals(paramterName)) {
                sb.append(" 排序字段"); //$NON-NLS-1$
            } else if ("distinct".equals(paramterName)) {
                sb.append(" 是否过滤重复数据");
            } else if ("criteria".equals(paramterName)) {
                sb.append(" 过滤条件实例");
            } else {
                sb.append("  数据对象");
            }
            method.addJavaDocLine(sb.toString());
        }
        method.addJavaDocLine(" */"); //$NON-NLS-1$
    }


    //model对象中字段的注释
    @Override
    public void addFieldComment(Field field, IntrospectedTable introspectedTable,
                                IntrospectedColumn introspectedColumn) {
        // 添加字段注释
        StringBuilder sb = new StringBuilder();
        field.addJavaDocLine("/**");
        if (introspectedColumn.getRemarks() != null) {
            field.addJavaDocLine(" * " + introspectedColumn.getRemarks());
        }
        sb.append(" * 表字段 : ");
        //对应表名称
        sb.append(introspectedTable.getFullyQualifiedTable());
        sb.append('.');
        //对应表中字段的名称
        sb.append(introspectedColumn.getActualColumnName());
        field.addJavaDocLine(sb.toString());
        field.addJavaDocLine(" */");
    }

    public void addModelClassComment(TopLevelClass innerClass,
                                     IntrospectedTable introspectedTable) {
        StringBuilder sb = new StringBuilder();
        innerClass.addJavaDocLine("/**");
        sb.append(" * 描述: ");
        sb.append(introspectedTable.getFullyQualifiedTable()).append(" 表的实体类");
        innerClass.addJavaDocLine(sb.toString().replace("\n", " "));
        sb.setLength(0);
        String remarks = introspectedTable.getRemarks();
        if (remarks != null && !remarks.isEmpty()) {
            sb.append(" * ").append(introspectedTable.getRemarks());
            innerClass.addJavaDocLine(sb.toString().replace("\n", " "));
            sb.setLength(0);
        }
        sb.append(" * @since ");
        sb.append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        innerClass.addJavaDocLine(sb.toString().replace("\n", " "));
        innerClass.addJavaDocLine(" */");
    }

    @Override
    public void addGetterComment(Method method,
                                 IntrospectedTable introspectedTable,
                                 IntrospectedColumn introspectedColumn) {
        StringBuilder sb = new StringBuilder();
        method.addJavaDocLine("/**"); //$NON-NLS-1$
        sb.append(" * 获取 "); //$NON-NLS-1$
        sb.append(introspectedColumn.getRemarks()).append(" 字段:");
        sb.append(introspectedTable.getFullyQualifiedTable());
        sb.append('.');
        sb.append(introspectedColumn.getActualColumnName());
        method.addJavaDocLine(sb.toString());
        method.addJavaDocLine(" *"); //$NON-NLS-1$
        sb.setLength(0);
        sb.append(" * @return "); //$NON-NLS-1$
        sb.append(introspectedTable.getFullyQualifiedTable());
        sb.append('.');
        sb.append(introspectedColumn.getActualColumnName());
        sb.append(", ");
        sb.append(introspectedColumn.getRemarks());
        method.addJavaDocLine(sb.toString());
        method.addJavaDocLine(" */"); //$NON-NLS-1$
    }

    @Override
    public void addSetterComment(Method method,
                                 IntrospectedTable introspectedTable,
                                 IntrospectedColumn introspectedColumn) {
        StringBuilder sb = new StringBuilder();
        method.addJavaDocLine("/**"); //$NON-NLS-1$
        sb.append(" * 设置 ");  //$NON-NLS-1$
        sb.append(introspectedColumn.getRemarks()).append(" 字段:");
        sb.append(introspectedTable.getFullyQualifiedTable());
        sb.append('.');
        sb.append(introspectedColumn.getActualColumnName());
        method.addJavaDocLine(sb.toString());
        method.addJavaDocLine(" *"); //$NON-NLS-1$
        List<Parameter> parameterList = method.getParameters();
        if (parameterList != null && !parameterList.isEmpty()) {
            Parameter parameter = parameterList.get(0);
            sb.setLength(0);
            sb.append(" * @param "); //$NON-NLS-1$
            sb.append(parameter.getName());
            sb.append(" the value for "); //$NON-NLS-1$
            sb.append(introspectedTable.getFullyQualifiedTable());
            sb.append('.');
            sb.append(introspectedColumn.getActualColumnName());
            sb.append(", ");
            sb.append(introspectedColumn.getRemarks());
        }
        method.addJavaDocLine(sb.toString());
        method.addJavaDocLine(" */"); //$NON-NLS-1$
    }

    @Override
    public void addComment(XmlElement xmlElement) {
        //xmlElement.addElement(new TextElement("<!--")); //$NON-NLS-1$
        //xmlElement.addElement(new TextElement(MergeConstants.NEW_ELEMENT_TAG));
        //xmlElement.addElement(new TextElement("-->")); //$NON-NLS-1$
    }
}
