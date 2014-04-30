package net.thecodemaster.sap.graph;

import java.util.Arrays;
import java.util.List;

import net.thecodemaster.sap.utils.Creator;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.IPackageBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.Statement;

/**
 * @author Luciano Sampaio
 */
public class BindingResolver {

  private BindingResolver() {
  }

  /**
   * Returns the type binding representing the class or interface that declares this method or constructor.
   * 
   * @param methodBinding
   * @return the binding of the class or interface that declares this method or constructor
   */
  public static ITypeBinding getDeclaringClass(IMethodBinding methodBinding) {
    return (null != methodBinding) ? methodBinding.getDeclaringClass() : null;
  }

  public static String getName(MethodDeclaration node) {
    return getName(resolveBinding(node));
  }

  /**
   * Returns the name of the method declared in this binding. The method name is always a simple identifier. The name of a constructor is always
   * the same as the declared name of its declaring class.
   * 
   * @param methodBinding
   * @return the name of this method, or the declared name of this constructor's declaring class.
   */
  public static String getName(IMethodBinding methodBinding) {
    return (null != methodBinding) ? methodBinding.getName() : null;
  }

  /**
   * Returns the name of the package represented by this binding. For named packages, this is the fully qualified package name (using "." for
   * separators). For unnamed packages, this is an empty string.
   * 
   * @param pkg
   * @return the name of the package represented by this binding, or an empty string for an unnamed package.
   */
  public static String getName(IPackageBinding pkg) {
    return (null != pkg) ? pkg.getName() : null;
  }

  /**
   * Returns the unqualified name of the type represented by this binding if it has one.
   * 
   * @param typeBinding
   * @return the unqualified name of the type represented by this binding, or the empty string if it has none.
   */
  public static String getName(ITypeBinding typeBinding) {
    return (null != typeBinding) ? typeBinding.getName() : null;
  }

  /**
   * Returns the binding for the package in which this type is declared.
   * 
   * The package of a recovered type reference binding is either the package of the enclosing type, or, if the type name is the name of a
   * well-known type, the package of the matching well-known type.
   * 
   * @param typeBinding
   * @return the binding for the package in which this class, interface, enum, or annotation type is declared, or null if this type binding
   *         represents a primitive type, an array type, the null type, a type variable, a wild card type, a capture binding.
   */
  public static IPackageBinding getPackage(ITypeBinding typeBinding) {
    return (null != typeBinding) ? typeBinding.getPackage() : null;
  }

  public static IMethodBinding resolveBinding(MethodDeclaration node) {
    return (null != node) ? node.resolveBinding() : null;
  }

  public static IMethodBinding resolveMethodBinding(MethodInvocation node) {
    return (null != node) ? node.resolveMethodBinding() : null;
  }

  public static IMethodBinding resolveConstructorBinding(ClassInstanceCreation node) {
    return (null != node) ? node.resolveConstructorBinding() : null;
  }

  public static String getQualifiedName(IMethodBinding node) {
    String qualifiedName = null;

    ITypeBinding clazz = getDeclaringClass(node);
    if (null != clazz) {

      IPackageBinding pkg = getPackage(clazz);
      if (null != pkg) {
        qualifiedName = String.format("%s.%s", getName(pkg), getName(clazz));
      }
    }

    return qualifiedName;
  }

  public static String getName(Expression expr) {
    if (expr.getNodeType() == ASTNode.METHOD_INVOCATION) {
      return getName(((MethodInvocation) expr).resolveMethodBinding());
    }

    return null;
  }

  public static String getQualifiedName(Expression expr) {
    if (expr.getNodeType() == ASTNode.METHOD_INVOCATION) {
      return getQualifiedName(((MethodInvocation) expr).resolveMethodBinding());
    }

    return null;
  }

  public static List<Expression> getParameterTypes(Expression expr) {
    if (expr.getNodeType() == ASTNode.METHOD_INVOCATION) {
      return getParameterTypes(((MethodInvocation) expr).arguments());
    }

    return Creator.newList();
  }

  public static List<ITypeBinding> getParameterTypes(MethodDeclaration node) {
    IMethodBinding methodBinding = resolveBinding(node);

    return (null != methodBinding) ? Arrays.asList(methodBinding.getParameterTypes()) : null;
  }

  /**
   * This method was created because the list returned from the arguments is not generic.
   * 
   * @param arguments The live ordered list of argument expressions in this method invocation expression.
   * @return List<Expression>
   */
  @SuppressWarnings("unchecked")
  public static List<Expression> getParameterTypes(List<?> arguments) {
    List<Expression> expressions = Creator.newList();

    if (null != arguments) {
      expressions = (List<Expression>) arguments;
    }

    return expressions;
  }

  public static CompilationUnit findParentCompilationUnit(ASTNode node) {
    return (CompilationUnit) findAncestor(node, ASTNode.COMPILATION_UNIT);
  }

  public static ASTNode findAncestor(ASTNode node, int nodeType) {
    while ((node != null) && (node.getNodeType() != nodeType)) {
      node = node.getParent();
    }
    return node;
  }

  /**
   * Gets the surrounding {@link Statement} of this a {@link SimpleName} ast
   * node.
   * 
   * @param reference
   *          any {@link SimpleName}
   * @return the surrounding {@link Statement} as found in the AST
   *         parent-child hierarchy
   */
  public static Statement getParentStatement(SimpleName reference) {
    ASTNode node = reference;
    while (!(node instanceof Statement)) {
      node = node.getParent();
    }
    return (Statement) node;
  }

  /**
   * Finds the parent {@link Block} of a {@link Statement}.
   * 
   * @param s
   *          the {@link Statement} to find the its parent {@link Block} for
   * @return the parent block of {@code s}
   */
  public static Block getParentBlock(Statement s) {
    ASTNode node = s;
    while (!(node instanceof Block)) {
      node = node.getParent();
    }
    return (Block) node;
  }

  public static String getQualifiedName(MethodDeclaration node) {
    return getQualifiedName(resolveBinding(node));
  }

}
