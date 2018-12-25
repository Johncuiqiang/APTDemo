package com.example.aptcompiler;

import com.example.aptannotation.BindApi;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.JavaFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;


/**
 * @Author : cuiqiang
 * @DATE : 2018/12/25 14:25
 * @Description :
 */
@AutoService(Processor.class)
public class AptApiProcessor extends AbstractProcessor {

    private Messager mMessager;
    private Filer mFilerUtils;       // 文件管理工具类
    private Elements mElementsUtils;  // Element处理工具类
    private Map<String, ClassCreatorProxy> mBindMap = new HashMap<>();

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        mMessager = processingEnv.getMessager();
        mFilerUtils = processingEnv.getFiler();
        mElementsUtils = processingEnv.getElementUtils();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        HashSet<String> supportTypes = new LinkedHashSet<>();
        supportTypes.add(BindApi.class.getCanonicalName());
        return supportTypes;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        mMessager.printMessage(Diagnostic.Kind.NOTE, "processing...");
        mBindMap.clear();
        if (annotations != null && annotations.size() != 0) {
            Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(BindApi.class);//获得被BindView注解标记的element
            categories(elements);//对不同的Activity进行分类

            //对不同的Activity生成不同的帮助类
//             输出帮助类的java文件，在这个例子中就是MainActivity$$AptApiManager.java文件
//             输出的文件在build->source->apt->目录下,IO流文件输出
//                try {
//                    JavaFileObject jfo = mFilerUtils.createSourceFile(helperClassName, typeElement);
//                    Writer writer = jfo.openWriter();
//                    writer.write(code);
//                    writer.flush();
//                    writer.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
            //通过javapoet生成
            for (String key : mBindMap.keySet()) {
                ClassCreatorProxy proxyInfo = mBindMap.get(key);
                JavaFile javaFile = JavaFile.builder(proxyInfo.getPackageName(), proxyInfo.generateJavaCode()).build();
                try {
                    //　生成文件
                    javaFile.writeTo(processingEnv.getFiler());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return true;
        }
        return false;
    }

    /**
     * VariableElement 是field，parameter
     * ExecutableElement 是method List<? extends VariableElement>  getParameters()用于获取方法的参数元素，
     * 每个元素是一个VariableElement
     * typeElement 是一个class
     * <p>
     * ElementKind  getKind() 返回element的类型，判断是哪种element
     * Set<Modifier> getModifiers() 获取修饰关键字,入public static final等关键字
     * Name  getSimpleName() 获取名字，不带包名
     */
    private void categories(Set<? extends Element> elements) {
        for (Element element : elements) {  //遍历每一个element
            VariableElement variableElement = (VariableElement) element;    //被@BindView标注的应当是变量，这里简单的强制类型转换
            TypeElement classElement = (TypeElement) variableElement.getEnclosingElement(); //获取代表Activity的TypeElement
            String fullClassName = classElement.getQualifiedName().toString();
            //elements的信息保存到mProxyMap中
            ClassCreatorProxy proxy = mBindMap.get(fullClassName);
            if (proxy == null) {
                proxy = new ClassCreatorProxy(mElementsUtils, classElement);
                mBindMap.put(fullClassName, proxy);
            }
            BindApi bindAnnotation = variableElement.getAnnotation(BindApi.class);    //获取到一个变量的注解
            int id = bindAnnotation.value();    //取出注解中的value值，这个值就是这个view要绑定的xml中的id
            proxy.putElement(id, variableElement);    //把要绑定的View的信息存进views中
        }
    }


}
