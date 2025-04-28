package lib

import com.github.javaparser.ast.`type`.TypeParameter
import com.github.javaparser.ast.body.{ClassOrInterfaceDeclaration, FieldDeclaration, MethodDeclaration, VariableDeclarator}
import com.github.javaparser.ast.expr.ObjectCreationExpr
import com.github.javaparser.ast.visitor.VoidVisitorAdapter
import com.github.javaparser.ast.{ImportDeclaration, PackageDeclaration}

import scala.jdk.CollectionConverters.*

class MyVoidVisitorAdapter extends VoidVisitorAdapter[AnyRef] {

  private var classOrInt: Set[String] = Set()
  private var packageDecl: Set[String] = Set()
  private var fieldsDecl: Set[String] = Set()
  private var methodsDecl: Set[String] = Set()
  private var objectCreation: Set[String] = Set()
  private var variableDecl: Set[String] = Set()
  private var typePar: Set[String] = Set()
  private var imports: Set[String] = Set()

  /**
   * Finding a type in a class/interface declaration
   */
  override def visit(n: ClassOrInterfaceDeclaration, arg: AnyRef): Unit = {
    super.visit(n, arg)
    //System.out.println("type " + n.getName + " (class/int decl)")
    classOrInt = classOrInt + ("type " + n.getName + " (class/int decl)")
  }

  /**
   * Package declaration
   */
  override def visit(n: PackageDeclaration, arg: AnyRef): Unit = {
    super.visit(n, arg)
    //System.out.println("package " + n.getName + " (package decl)")
    packageDecl = packageDecl + ("package " + n.getName + " (package decl)")
  }

  /**
   * Finding a type in a field declaration
   */
  override def visit(n: FieldDeclaration, arg: AnyRef): Unit = {
    super.visit(n, arg)
    val vd: VariableDeclarator = n.getChildNodes.get(0).asInstanceOf[VariableDeclarator]
    //System.out.println("type " + vd.getType.asString + " (field decl)")
    fieldsDecl = fieldsDecl + ("type " + vd.getType.asString + " (field decl)")
  }

  /**
   * Finding types in methods declaration
   */
  override def visit(n: MethodDeclaration, arg: AnyRef): Unit = {
    super.visit(n, arg)
    // System.out.println("method: " + n.toString());
    for (p <- n.getParameters.asScala) {
      //System.out.println("type " + p.getType.asString + " (method decl, param type)")
      methodsDecl = methodsDecl + ("type " + p.getType.asString + " (method decl, param type)")
    }
    //System.out.println("return type: " + n.getType.asString + " (method decl, return type)")
    methodsDecl = methodsDecl + ("return type: " + n.getType.asString + " (method decl, return type)")
  }

  /**
   * Finding type in object creation
   */
  override def visit(n: ObjectCreationExpr, arg: AnyRef): Unit = {
    super.visit(n, arg)
    val interfaceOrClassType = n.getChildNodes.get(0)
    //System.out.println("type " + interfaceOrClassType + " (obj creation decl)")
    objectCreation = objectCreation + ("type " + interfaceOrClassType.toString + " (obj creation decl)")
  }

  /**
   * Finding types in variable declaration
   */
  override def visit(n: VariableDeclarator, arg: AnyRef): Unit = {
    super.visit(n, arg)
    val t = n.getType
    //System.out.println("type " + n.getType.asString + " (var decl)")
    variableDecl = variableDecl + ("type " + n.getType.asString + " (var decl)")
  }

  /**
   * Finding types in type parameter
   */
  override def visit(n: TypeParameter, arg: AnyRef): Unit = {
    super.visit(n, arg)
    //System.out.println("type " + n.asString + "(type decl)")
    typePar = typePar + ("type " + n.asString + "(type decl)")
  }

  /**
   * Finding types in import declaration
   */
  override def visit(n: ImportDeclaration, arg: AnyRef): Unit = {
    super.visit(n, arg)
    if (!(n.isAsterisk)) {
      val typeName = n.getChildNodes.get(0)
      val packageName = typeName.getChildNodes.get(0)
      //System.out.println("type " + typeName + " package: " + packageName + " (import)")
      imports = imports + ("type " + typeName.toString + " package: " + packageName.toString + " (import)")
    }
    else {
      val packageName = n.getChildNodes.get(0)
      //System.out.println("package " + packageName + " (import)")
      imports = imports + ("package " + packageName.toString + " (import)")
    }
  }

  def getMap: Map[String, Set[String]] =
    Map("Class or Interface" -> classOrInt,
      "Packages" -> packageDecl,
      "Fields" -> fieldsDecl,
      "Methods" -> methodsDecl,
      "Object creation" -> objectCreation,
      "Variables" -> variableDecl,
      "Types" -> typePar,
      "Imports" -> imports)

}
