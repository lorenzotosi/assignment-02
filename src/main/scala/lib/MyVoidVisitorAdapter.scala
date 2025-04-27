package lib

import com.github.javaparser.ast.`type`.TypeParameter
import com.github.javaparser.ast.body.{ClassOrInterfaceDeclaration, FieldDeclaration, MethodDeclaration, VariableDeclarator}
import com.github.javaparser.ast.expr.{Name, ObjectCreationExpr, SimpleName}
import com.github.javaparser.ast.visitor.VoidVisitorAdapter
import com.github.javaparser.ast.{ImportDeclaration, PackageDeclaration}

import scala.jdk.CollectionConverters.*

object MyVoidVisitorAdapter extends VoidVisitorAdapter[AnyRef] {

  /**
   * Finding a type in a class/interface declaration
   */
  override def visit(n: ClassOrInterfaceDeclaration, arg: AnyRef): Unit = {
    super.visit(n, arg)
    System.out.println("type " + n.getName + " (class/int decl)")
  }

  def getName(n: ClassOrInterfaceDeclaration, arg: AnyRef): SimpleName = {
    super.visit(n, arg)
    n.getName
  }

  /**
   * Package declaration
   */
  override def visit(n: PackageDeclaration, arg: AnyRef): Unit = {
    super.visit(n, arg)
    System.out.println("package " + n.getName + " (package decl)")
  }
  def getPackageName(n: PackageDeclaration, arg: AnyRef): Name = {
    super.visit(n, arg)
    n.getName
  }

  /**
   * Finding a type in a field declaration
   */
  override def visit(n: FieldDeclaration, arg: AnyRef): Unit = {
    super.visit(n, arg)
    val vd: VariableDeclarator = n.getChildNodes.get(0).asInstanceOf[VariableDeclarator]
    System.out.println("type " + vd.getType.asString + " (field decl)")
  }
  def getFields(n: FieldDeclaration, arg: AnyRef): String = {
    super.visit(n, arg)
    val vd: VariableDeclarator = n.getChildNodes.get(0).asInstanceOf[VariableDeclarator]
    vd.getType.asString
  }

  /**
   * Finding types in methods declaration
   */
  override def visit(n: MethodDeclaration, arg: AnyRef): Unit = {
    super.visit(n, arg)
    // System.out.println("method: " + n.toString());
    for (p <- n.getParameters.asScala) {
      System.out.println("type " + p.getType.asString + " (method decl, param type)")
    }
    System.out.println("return type: " + n.getType.asString + " (method decl, return type)")
  }

  def getMethodsParameters(n: MethodDeclaration, arg: AnyRef): List[String] = {
    super.visit(n, arg)
    n.getParameters.asScala.toList.map(x => x.getType.asString())
  }
  def getMethodReturn(n: MethodDeclaration, arg: AnyRef): String  = {
    super.visit(n, arg)
    n.getType.asString
  }

  /**
   * Finding type in object creation
   */
  override def visit(n: ObjectCreationExpr, arg: AnyRef): Unit = {
    super.visit(n, arg)
    val interfaceOrClassType = n.getChildNodes.get(0)
    System.out.println("type " + interfaceOrClassType + " (obj creation decl)")
  }
  def getObjectCreationExpr(n: ObjectCreationExpr, arg: AnyRef): String = {
    super.visit(n, arg)
    n.getChildNodes.get(0).toString
  }

  /**
   * Finding types in variable declaration
   */
  override def visit(n: VariableDeclarator, arg: AnyRef): Unit = {
    super.visit(n, arg)
    val t = n.getType
    System.out.println("type " + n.getType.asString + " (var decl)")
  }
  def getTypeVarDeclaration(n: VariableDeclarator, arg: AnyRef): String = {
    super.visit(n, arg)
    n.getType.asString
  }

  /**
   * Finding types in type parameter
   */
  override def visit(n: TypeParameter, arg: AnyRef): Unit = {
    super.visit(n, arg)
    System.out.println("type " + n.asString + "(type decl)")
  }

  /**
   * Finding types in import declaration
   */
  override def visit(n: ImportDeclaration, arg: AnyRef): Unit = {
    super.visit(n, arg)
    if (!(n.isAsterisk)) {
      val typeName = n.getChildNodes.get(0)
      val packageName = typeName.getChildNodes.get(0)
      System.out.println("type " + typeName + " package: " + packageName + " (import)")
    }
    else {
      val packageName = n.getChildNodes.get(0)
      System.out.println("package " + packageName + " (import)")
    }
  }
}
