package com.example.aptcompiler;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;

/**
 * @Author : cuiqiang
 * @DATE : 2018/12/25 14:47
 * @Description :
 */
public class ClassCreatorProxy {

    private String mBindingClassName;
    private String mPackageName;
    private TypeElement mTypeElement;
    private HashMap<Integer,VariableElement> mVariableElementMap = new HashMap<>();

    public ClassCreatorProxy(Elements elementUtils, TypeElement classElement) {
        this.mTypeElement = classElement;
        PackageElement packageElement = elementUtils.getPackageOf(mTypeElement);
        String packageName = packageElement.getQualifiedName().toString();
        String className = mTypeElement.getSimpleName().toString();
        this.mPackageName = packageName;
        this.mBindingClassName = className + "_ViewBinding";
    }

    public void putElement(int id, VariableElement element) {
        mVariableElementMap.put(id, element);
    }


    public TypeSpec generateJavaCode() {
        TypeSpec bindingClass = TypeSpec.classBuilder(mBindingClassName)
                .addModifiers(Modifier.PUBLIC)
                .addMethod(generateMethods())
                .build();
        return bindingClass;

    }

    /**
     * 创建findviewbyid的代码
     * @return
     */
    private MethodSpec generateMethods() {
        ClassName host = ClassName.bestGuess(mTypeElement.getQualifiedName().toString());
        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("bind")
                .addModifiers(Modifier.PUBLIC)
                .returns(void.class)
                .addParameter(host, "host");

        for (int id : mVariableElementMap.keySet()) {
            VariableElement element = mVariableElementMap.get(id);
            String name = element.getSimpleName().toString();
            String type = element.asType().toString();
            methodBuilder.addCode("host." + name + " = " + "(" + type + ")(((android.app.Activity)host).findViewById( " + id + "));");
        }
        return methodBuilder.build();
    }

    public String getPackageName() {
        return mPackageName;
    }

    /**
     * 用stringbuilder拼写这个方法
     */
//    private String generateCode(TypeElement typeElement) {
//        String rawClassName = typeElement.getSimpleName().toString(); //获取要绑定的View所在类的名称
//        String packageName = ((PackageElement) mElementsUtils.getPackageOf(typeElement)).getQualifiedName().toString(); //获取要绑定的View所在类的包名
//        String helperClassName = rawClassName + "$$AptApiManager";   //要生成的帮助类的名称
//
//        StringBuilder builder = new StringBuilder();
//        builder.append("package ").append(packageName).append(";\n");   //构建定义包的代码
//        builder.append("import com.example.aptapi.IBindHelper;\n\n"); //构建import类的代码
//        builder.append("import android.util.Log;\n\n");
//
//        builder.append("public class ").append(helperClassName).append(" implements ").append("IBindHelper");   //构建定义帮助类的代码
//        builder.append(" {\n"); //代码格式，可以忽略
//        builder.append("\t@Override\n");    //声明这个方法为重写IBindHelper中的方法
//        builder.append("\tpublic void inject(" + "Object" + " target ) {\n");   //构建方法的代码
//        for (ViewInfo viewInfo : mToBindMap.get(typeElement)) { //遍历每一个需要绑定的view
//            builder.append("\t\t"); //代码格式，可以忽略
//            builder.append(rawClassName + " substitute = " + "(" + rawClassName + ")" + "target;\n");    //强制类型转换
//
//            builder.append("\t\t"); //代码格式，可以忽略
//            builder.append("substitute." + viewInfo.viewName).append(" = ");    //构建赋值表达式
//            builder.append("substitute.findViewById(" + viewInfo.id + ");\n");  //构建赋值表达式
//        }
//        builder.append("\t}\n");    //代码格式，可以忽略
//        builder.append('\n');   //代码格式，可以忽略
//        builder.append("}\n");  //代码格式，可以忽略
//
//        return builder.toString();
//    }

}
