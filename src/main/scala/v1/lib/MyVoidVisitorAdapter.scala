package v1.lib

import com.github.javaparser.ast.`type`.TypeParameter
import com.github.javaparser.ast.body.{ClassOrInterfaceDeclaration, FieldDeclaration, MethodDeclaration, VariableDeclarator}
import com.github.javaparser.ast.expr.ObjectCreationExpr
import com.github.javaparser.ast.visitor.VoidVisitorAdapter
import com.github.javaparser.ast.{ImportDeclaration, PackageDeclaration}

import scala.jdk.CollectionConverters.*

class MyVoidVisitorAdapter extends VoidVisitorAdapter[AnyRef] {

  private var classOrInt: List[String] = List()
  private var packageDecl: List[String] = List()
  private var fieldsDecl: List[String] = List()
  private var methodsDecl: List[String] = List()
  private var objectCreation: List[String] = List()
  private var variableDecl: List[String] = List()
  private var typePar: List[String] = List()
  private var imports: List[String] = List()

  /**
   * Finding a type in a class/interface declaration
   */
  override def visit(n: ClassOrInterfaceDeclaration, arg: AnyRef): Unit = {
    super.visit(n, arg)
    //System.out.println("type " + n.getName + " (class/int decl)")
    classOrInt = ("type " + n.getName + " (class/int decl)") :: classOrInt
  }

  /**
   * Package declaration
   */
  override def visit(n: PackageDeclaration, arg: AnyRef): Unit = {
    super.visit(n, arg)
    //System.out.println("package " + n.getName + " (package decl)")
    packageDecl = ("package " + n.getName + " (package decl)") :: packageDecl 
  }

  /**
   * Finding a type in a field declaration
   */
  override def visit(n: FieldDeclaration, arg: AnyRef): Unit = {
    super.visit(n, arg)
    n.getVariables.forEach((vd: VariableDeclarator) => {
      fieldsDecl = ("type " + vd.getType.asString + " (field decl)") :: fieldsDecl
    })
    //System.out.println("type " + vd.getType.asString + " (field decl)")
  }

  /**
   * Finding types in methods declaration
   */
  override def visit(n: MethodDeclaration, arg: AnyRef): Unit = {
    super.visit(n, arg)
    // System.out.println("method: " + n.toString());
    for (p <- n.getParameters.asScala) {
      //System.out.println("type " + p.getType.asString + " (method decl, param type)")
      methodsDecl = ("type " + p.getType.asString + " (method decl, param type)") :: methodsDecl
    }
    //System.out.println("return type: " + n.getType.asString + " (method decl, return type)")
    methodsDecl = ("return type: " + n.getType.asString + " (method decl, return type)") :: methodsDecl
  }

  /**
   * Finding type in object creation
   */
  override def visit(n: ObjectCreationExpr, arg: AnyRef): Unit = {
    super.visit(n, arg)
    val interfaceOrClassType = n.getChildNodes.get(0)
    //System.out.println("type " + interfaceOrClassType + " (obj creation decl)")
    objectCreation = ("type " + interfaceOrClassType.toString + " (obj creation decl)") :: objectCreation
  }

  /**
   * Finding types in variable declaration
   */
  override def visit(n: VariableDeclarator, arg: AnyRef): Unit = {
    super.visit(n, arg)
    val t = n.getType
    //System.out.println("type " + n.getType.asString + " (var decl)")
    variableDecl = ("type " + n.getType.asString + " (var decl)") :: variableDecl
  }

  /**
   * Finding types in type parameter
   */
  override def visit(n: TypeParameter, arg: AnyRef): Unit = {
    super.visit(n, arg)
    //System.out.println("type " + n.asString + "(type decl)")
    typePar = ("type " + n.asString + "(type decl)") :: typePar
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
      imports = ("type " + typeName.toString + " package: " + packageName.toString + " (import)") :: imports
    }
    else {
      val packageName = n.getChildNodes.get(0)
      //System.out.println("package " + packageName + " (import)")
      imports = ("package " + packageName.toString + " (import)") :: imports
    }
  }

  def getMap: Map[String, List[String]] =
    Map("Class or Interface" -> classOrInt,
      "Packages" -> packageDecl,
      "Fields" -> fieldsDecl,
      "Methods" -> methodsDecl,
      "Object creation" -> objectCreation,
      "Variables" -> variableDecl,
      "Types" -> typePar,
      "Imports" -> imports)

}
