package lib

import com.github.javaparser.ast.ImportDeclaration
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
          var foundImp: Boolean = false
          sets.foreach(e =>
            if e.contains(vd.getType.asString) && e.contains("Import") then
              sets = sets + (vd.getType.asString + " (From: " + e.substring(8) + ")")
              foundImp = true
          )
          if !foundImp then
            sets = sets + (vd.getType.asString + " (From: Could not Resolve)")
            println(s"Errore durante la risoluzione del tipo per la variabile '${vd.getName.asString}' nel campo '${n.toString}': ${e.getMessage}")
      }
    })
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
          var foundImp: Boolean = false
          sets.foreach(e =>
            if e.contains(p.getType.asString) && e.contains("Import") then
              sets = sets + (p.getType.asString + " (From: " + e.substring(8) + ")")
              foundImp = true
          )
          if !foundImp then
            sets = sets + (p.getType.asString + " (From: Could not resolve)")
            println(s"Errore durante la risoluzione del tipo per il parametro '${p.getName.asString}' nel metodo '${n.getName.asString}': ${e.getMessage}")
      }
    }
    try {
      val p = n.getType.resolve()
      sets = sets + (n.getType.asString + " (From: " + p.describe() + ")")
    } catch {
      case e: Exception =>
        // Gestione dell'eccezione
        var foundImp: Boolean = false
        sets.foreach(e =>
          if e.contains(n.getType.asString) && e.contains("Import") then
            sets = sets + (n.getType.asString + " (From: " + e.substring(8) + ")")
            foundImp = true
        )
        if !foundImp then
          sets = sets + (n.getType.asString + " (From: Could not resolve)")
          println(s"Errore durante la risoluzione del tipo di ritorno nel metodo '${n.getName.asString}': ${e.getMessage}")
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
        var foundImp: Boolean = false
        sets.foreach(e =>
          if e.contains(n.getType.asString) && e.contains("Import") then
            sets = sets + (n.getType.asString + " (From: " + e.substring(8) + ")")
            foundImp = true
        )
        if !foundImp then
          sets = sets + (interfaceOrClassType.toString + " (From: Could not resolve)")
          println(s"Errore durante la risoluzione del tipo ObjCreation: ${e.getMessage}")
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
        var foundImp: Boolean = false
        sets.foreach(e =>
          if e.contains(n.getType.asString) && e.contains("Import") then
            sets = sets + (n.getType.asString + " (From: " + e.substring(8) + ")")
            foundImp = true
        )
        if !foundImp then
          sets = sets + (n.getType.asString + " (From: Could not resolve)")
          println(s"Errore durante la risoluzione del tipo VarDec: ${e.getMessage}")
    }
  }

  override def visit(n: ImportDeclaration, arg: AnyRef): Unit = {
    super.visit(n, arg)
    val imp = n.getNameAsString
    sets = sets + ("Import: " + imp)
  }

  def getSet: Set[String] = sets

}
