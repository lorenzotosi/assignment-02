package lib

import com.github.javaparser.ast.PackageDeclaration
import com.github.javaparser.ast.`type`.TypeParameter
import com.github.javaparser.ast.body.{ClassOrInterfaceDeclaration, FieldDeclaration, MethodDeclaration, VariableDeclarator}
import com.github.javaparser.ast.expr.ObjectCreationExpr
import com.github.javaparser.ast.visitor.VoidVisitorAdapter

import scala.jdk.CollectionConverters.*

class MyVoidVisitorAdapter extends VoidVisitorAdapter[AnyRef] {

  private var sets: Set[String] = Set()

  /**
   * Finding a type in a class/interface declaration
   */
  override def visit(n: ClassOrInterfaceDeclaration, arg: AnyRef): Unit = {
    super.visit(n, arg)
    sets = sets + (n.getName.asString() + " (From: " + n.getFullyQualifiedName.get() + ")")
  }

  /**
   * Package declaration
   */
//  override def visit(n: PackageDeclaration, arg: AnyRef): Unit = {
//    super.visit(n, arg)
//    sets =  sets + n.getName.asString()
//  }

  /**
   * Finding a type in a field declaration
   */
  override def visit(n: FieldDeclaration, arg: AnyRef): Unit = {
    super.visit(n, arg)
    n.getVariables.forEach((vd: VariableDeclarator) => {
      try {
        val p = vd.getType.resolve()
        sets = sets + (vd.getType.asString + " (From: " + p.describe() + ")")
      } catch {
        case e: Exception =>
          // Gestione dell'eccezione
          println(s"Errore durante la risoluzione del tipo: ${e.getMessage}")
      }
    })
    //System.out.println("type " + vd.getType.asString + " (field decl)")
  }

  /**
   * Finding types in methods declaration
   */
  override def visit(n: MethodDeclaration, arg: AnyRef): Unit = {
    super.visit(n, arg)
    for (p <- n.getParameters.asScala) {
      try {
        val l = p.getType.resolve()
        sets = sets + (p.getType.asString + " (From: " + l.describe() + ")")
      } catch {
        case e: Exception =>
          // Gestione dell'eccezione
          println(s"Errore durante la risoluzione del tipo: ${e.getMessage}")
      }
    }
    try {
      val p = n.getType.resolve()
      sets = sets + (n.getType.asString + " (From: " + p.describe() + ")")
    } catch {
      case e: Exception =>
        // Gestione dell'eccezione
        println(s"Errore durante la risoluzione del tipo: ${e.getMessage}")
    }
  }

  /**
   * Finding type in object creation
   */
  override def visit(n: ObjectCreationExpr, arg: AnyRef): Unit = {
    super.visit(n, arg)
    val interfaceOrClassType = n.getChildNodes.get(0)
    try {
      val p = n.getType.resolve()
      sets = sets + (interfaceOrClassType.toString + " (From: " + p.describe() + ")")
    } catch {
      case e: Exception =>
        // Gestione dell'eccezione
        println(s"Errore durante la risoluzione del tipo: ${e.getMessage}")
    }

  }

  /**
   * Finding types in variable declaration
   */
  override def visit(n: VariableDeclarator, arg: AnyRef): Unit = {
    super.visit(n, arg)
    try {
      val t = n.getType
      val p = t.resolve()
      sets = sets + (n.getType.asString + " (From: " + p.describe() + ")")
    } catch {
      case e: Exception =>
        // Gestione dell'eccezione
        println(s"Errore durante la risoluzione del tipo: ${e.getMessage}")
    }
  }

  /**
   * Finding types in type parameter
   */
  override def visit(n: TypeParameter, arg: AnyRef): Unit = {
    super.visit(n, arg)
    sets = sets + n.asString
  }

  /**
   * Finding types in import declaration
   */
//  override def visit(n: ImportDeclaration, arg: AnyRef): Unit = {
//    super.visit(n, arg)
//    if (!(n.isAsterisk)) {
//      val typeName = n.getChildNodes.get(0)
//      val packageName = typeName.getChildNodes.get(0)
//      //System.out.println("type " + typeName + " package: " + packageName + " (import)")
//      imports = ("type " + typeName.toString + " package: " + packageName.toString + " (import)") :: imports
//    }
//    else {
//      val packageName = n.getChildNodes.get(0)
//      //System.out.println("package " + packageName + " (import)")
//      imports = ("package " + packageName.toString + " (import)") :: imports
//    }
//  }


  def getSet: Set[String] = sets

}
