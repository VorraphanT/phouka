package com.selfdualbrain.gui_framework.dof_editor

import com.selfdualbrain.dynamic_objects.DynamicObject
import org.jdesktop.swingx.treetable.AbstractTreeTableModel
import org.slf4j.LoggerFactory

import java.awt.EventQueue
import scala.language.existentials

/**
  * TreeTable model underlying the dof-tree dynamic editor.
  *
  * @param rootDynamicObject dynamic object to be edited in the tree (the tree is implied - it comes out by recursively traversing properties)
  */
class TTModel(rootDynamicObject: DynamicObject) extends AbstractTreeTableModel {
  private val log = LoggerFactory.getLogger("tree-table-model")

  val ttRootNode: TTNode.Root = TTNode.Root(this, parent = None, rootDynamicObject)

  override def getRoot: AnyRef = ttRootNode

  override def getChild(parent: Any, index: Int): AnyRef = {
    val node = parent.asInstanceOf[TTNode[_]]
    return node.childNodes(index)
  }

  override def getChildCount(parent: Any): Int = {
    val node = parent.asInstanceOf[TTNode[_]]
    return node.childNodes.size
  }

  override def getIndexOfChild(parent: Any, child: Any): Int = {
    if (parent == null)
      return -1
    if (child == null)
      return -1

    val parentNode = parent.asInstanceOf[TTNode[_]]
    val childNode = child.asInstanceOf[TTNode[_]]
    return parentNode.childNodes.indexOf(childNode)
  }

  override def getColumnCount: Int = 2

  override def getValueAt(node: Any, column: Int): AnyRef = {
    val ttNode = node.asInstanceOf[TTNode[_]]

    column match {
      case 0 => ttNode.displayedName
      case 1 => ttNode.value.asInstanceOf[AnyRef]
    }
  }

  override def setValueAt(value: Any, node: Any, column: Int): Unit = {
    this.privateSetValueAt(value.asInstanceOf[Option[Any]], node.asInstanceOf[TTNode[Any]], column)
  }

  private def privateSetValueAt[V](value: Option[V], node: TTNode[V], column: Int): Unit = {
    column match {
      case 0 => throw new RuntimeException("column 0 is not editable")
      case 1 =>
        if (node.isEditable)
          node.asInstanceOf[EditableTTNode[V]].value = value
        else
          throw new RuntimeException(s"Attempted to use non-editable TTNode as if it is editable - node instance was: $node")
    }
  }

  override def getColumnName(column: Int): String = super.getColumnName(column)

  override def isCellEditable(node: Any, column: Int): Boolean ={
    val ttNode = node.asInstanceOf[TTNode[_]]
    return column == 1 && ttNode.isEditable
  }

  def fireNodeChanged(node: TTNode[_]): Unit = {
    log.debug(s"fired <node changed> for node: $node")
    EventQueue invokeLater {
      () => modelSupport.firePathChanged(node.path)
    }
  }

  def fireSubtreeChanged(node: TTNode[_]): Unit = {
    log.debug(s"fired <subtree changed> under node: $node")
    EventQueue invokeLater {
      () => modelSupport.fireTreeStructureChanged(node.path)
    }
  }

  def fireNodeInserted(node: TTNode[_]): Unit = {
    log.debug(s"fired <node added> for node: $node")
    EventQueue invokeLater {
      () => modelSupport.fireChildAdded(node.parent.get.path, node.indexAmongSiblings, node)
    }
  }

  def fireNodeRemoved(node: TTNode[_], indexAmongSiblings: Int): Unit = {
    log.debug(s"fired <node removed> for node: $node")
    EventQueue invokeLater {
      () => modelSupport.fireChildRemoved(node.parent.get.path, indexAmongSiblings, node)
    }
  }

}
