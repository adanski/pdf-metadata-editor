package io.pdfx.app

import java.awt.Component
import java.awt.Container
import java.awt.Point
import java.awt.datatransfer.DataFlavor
import java.awt.datatransfer.Transferable
import java.awt.datatransfer.UnsupportedFlavorException
import java.awt.dnd.*
import java.awt.event.HierarchyEvent
import java.io.BufferedReader
import java.io.File
import java.io.IOException
import java.io.PrintStream
import java.net.URI
import java.util.*
import javax.swing.JComponent
import javax.swing.SwingUtilities
import javax.swing.border.Border

/**
 * This class makes it easy to drag and drop files from the operating
 * system to a Java program. Any <tt>java.awt.Component</tt> can be
 * dropped onto, but only <tt>javax.swing.JComponent</tt>s will indicate
 * the drop event with a changed border.
 *
 *
 * To use this class, construct a new <tt>FileDrop</tt> by passing
 * it the target component and a <tt>Listener</tt> to receive notification
 * when file(s) have been dropped. Here is an example:
 *
 *
 * `<pre>
 * JPanel myPanel = new JPanel();
 * new FileDrop( myPanel, new FileDrop.Listener()
 * {   public void filesDropped( java.io.File[] files )
 * {
 * // handle file drop
 * ...
 * }   // end filesDropped
 * }); // end FileDrop.Listener
</pre>` *
 *
 *
 * You can specify the border that will appear when files are being dragged by
 * calling the constructor with a <tt>javax.swing.border.Border</tt>. Only
 * <tt>JComponent</tt>s will show any indication with a border.
 *
 *
 * You can turn on some debugging features by passing a <tt>PrintStream</tt>
 * object (such as <tt>System.out</tt>) into the full constructor. A <tt>null</tt>
 * value will result in no extra debugging information being output.
 *
 *
 *
 *
 * I'm releasing this code into the Public Domain. Enjoy.
 *
 *
 * *Original author: Robert Harder, rharder@usa.net*
 *
 * 2007-09-12 Nathan Blomquist -- Linux (KDE/Gnome) support added.
 *
 * @author Robert Harder
 * @author rharder@users.sf.net
 * @version 1.0.1
 */
class FileDrop(
    out: PrintStream?,
    c: Component,
    recursive: Boolean,
    listener: Listener
) {
    @Transient
    private val normalBorder: Border? = null

    @Transient
    private var dropListener: DropTargetListener? = null

    /**
     * Constructs a [FileDrop] with a default light-blue border
     * and, if <var>c</var> is a [java.awt.Container], recursively
     * sets all elements contained within as drop targets, though only
     * the top level container will change borders.
     *
     * @param c        Component on which files will be dropped.
     * @param listener Listens for <tt>filesDropped</tt>.
     * @since 1.0
     */
    constructor(c: Component, listener: Listener) : this(
        null,  // Logging stream
        c,  // Drop target
        true,  // Recursive
        listener
    ) // end constructor

    /**
     * Full constructor with a specified border and debugging optionally turned on.
     * With Debugging turned on, more status messages will be displayed to
     * <tt>out</tt>. A common way to use this constructor is with
     * <tt>System.out</tt> or <tt>System.err</tt>. A <tt>null</tt> value for
     * the parameter <tt>out</tt> will result in no debugging output.
     *
     * @param out        PrintStream to record debugging info or null for no debugging.
     * @param c          Component on which files will be dropped.
     * @param dragBorder Border to use on <tt>JComponent</tt> when dragging occurs.
     * @param recursive  Recursively set children as drop targets.
     * @param listener   Listens for <tt>filesDropped</tt>.
     * @since 1.0
     */
    init {
        if (supportsDnD()) {   // Make a drop listener
            dropListener = object : DropTargetListener {
                override fun dragEnter(evt: DropTargetDragEvent) {
                    log(out, "FileDrop: dragEnter event.")

                    // Is this an acceptable drag event?
                    if (isDragOk(out, evt)) {
                        listener.dragEnter()

                        // Acknowledge that it's okay to enter
                        //evt.acceptDrag( java.awt.dnd.DnDConstants.ACTION_COPY_OR_MOVE );
                        evt.acceptDrag(DnDConstants.ACTION_COPY)
                        log(out, "FileDrop: event accepted.")
                    } // end if: drag ok
                    else {   // Reject the drag event
                        evt.rejectDrag()
                        log(out, "FileDrop: event rejected.")
                    } // end else: drag not ok
                } // end dragEnter

                fun getEventPoint(evt: DropTargetDragEvent): Point {
                    return SwingUtilities.convertPoint(evt.dropTargetContext.component, evt.location, c)
                }

                fun getEventPoint(evt: DropTargetDropEvent): Point {
                    return SwingUtilities.convertPoint(evt.dropTargetContext.component, evt.location, c)
                }

                override fun dragOver(evt: DropTargetDragEvent) {
                    listener.dragOver(getEventPoint(evt))
                } // end dragOver

                override fun drop(evt: DropTargetDropEvent) {
                    log(out, "FileDrop: drop event.")
                    try {   // Get whatever was dropped
                        val tr = evt.transferable

                        // Is it a file list?
                        if (tr.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                            // Say we'll take it.
                            //evt.acceptDrop ( java.awt.dnd.DnDConstants.ACTION_COPY_OR_MOVE );
                            evt.acceptDrop(DnDConstants.ACTION_COPY)
                            log(out, "FileDrop: file list accepted.")

                            // Get a useful list
                            val fileList = tr.getTransferData(DataFlavor.javaFileListFlavor) as List<File>
                            val iterator = fileList.iterator()

                            // Convert list to array
                            val files: Array<File> = fileList.toTypedArray()

                            // Alert listener to drop.
                            listener.filesDropped(files, getEventPoint(evt))

                            // Mark that drop is completed.
                            evt.dropTargetContext.dropComplete(true)
                            log(out, "FileDrop: drop complete.")
                        } // end if: file list
                        else  // this section will check for a reader flavor.
                        {
                            // Thanks, Nathan!
                            // BEGIN 2007-09-12 Nathan Blomquist -- Linux (KDE/Gnome) support added.
                            val flavors = tr.transferDataFlavors
                            var handled = false
                            for (zz in flavors.indices) {
                                if (flavors[zz].isRepresentationClassReader) {
                                    // Say we'll take it.
                                    //evt.acceptDrop ( java.awt.dnd.DnDConstants.ACTION_COPY_OR_MOVE );
                                    evt.acceptDrop(DnDConstants.ACTION_COPY)
                                    log(out, "FileDrop: reader accepted.")
                                    val reader = flavors[zz].getReaderForText(tr)
                                    val br = BufferedReader(reader)
                                    listener.filesDropped(createFileArray(br, out), getEventPoint(evt))

                                    // Mark that drop is completed.
                                    evt.dropTargetContext.dropComplete(true)
                                    log(out, "FileDrop: drop complete.")
                                    handled = true
                                    break
                                }
                            }
                            if (!handled) {
                                log(out, "FileDrop: not a file list or reader - abort.")
                                evt.rejectDrop()
                            }
                            // END 2007-09-12 Nathan Blomquist -- Linux (KDE/Gnome) support added.
                        } // end else: not a file list
                    } // end try
                    catch (io: IOException) {
                        log(out, "FileDrop: IOException - abort:")
                        io.printStackTrace(out)
                        evt.rejectDrop()
                    } // end catch IOException
                    catch (ufe: UnsupportedFlavorException) {
                        log(out, "FileDrop: UnsupportedFlavorException - abort:")
                        ufe.printStackTrace(out)
                        evt.rejectDrop()
                    } // end catch: UnsupportedFlavorException
                    finally {
                        // If it's a Swing component, reset its border
                        if (c is JComponent) {
                            c.border = normalBorder
                            log(out, "FileDrop: normal border restored.")
                        } // end if: JComponent
                    } // end finally
                } // end drop

                override fun dragExit(evt: DropTargetEvent) {
                    log(out, "FileDrop: dragExit event.")
                    listener.dragLeave()
                } // end dragExit

                override fun dropActionChanged(evt: DropTargetDragEvent) {
                    log(out, "FileDrop: dropActionChanged event.")
                    // Is this an acceptable drag event?
                    if (isDragOk(out, evt)) {   //evt.acceptDrag( java.awt.dnd.DnDConstants.ACTION_COPY_OR_MOVE );
                        evt.acceptDrag(DnDConstants.ACTION_COPY)
                        log(out, "FileDrop: event accepted.")
                    } // end if: drag ok
                    else {
                        evt.rejectDrag()
                        log(out, "FileDrop: event rejected.")
                    } // end else: drag not ok
                } // end dropActionChanged
            } // end DropTargetListener

            // Make the component (and possibly children) drop targets
            makeDropTarget(out, c, recursive)
        } // end if: supports dnd
        else {
            log(out, "FileDrop: Drag and drop is not supported with this JVM")
        } // end else: does not support DnD
    } // end constructor

    // END 2007-09-12 Nathan Blomquist -- Linux (KDE/Gnome) support added.
    private fun makeDropTarget(out: PrintStream?, c: Component, recursive: Boolean) {
        // Make drop target
        val dt = DropTarget()
        try {
            dt.addDropTargetListener(dropListener)
        } // end try
        catch (e: TooManyListenersException) {
            e.printStackTrace()
            log(out, "FileDrop: Drop will not work due to previous error. Do you have another listener attached?")
        } // end catch

        // Listen for hierarchy changes and remove the drop target when the parent gets cleared out.
        // end hierarchyChanged
        c.addHierarchyListener { evt: HierarchyEvent? ->
            log(out, "FileDrop: Hierarchy changed.")
            val parent: Component? = c.parent
            if (parent == null) {
                c.dropTarget = null
                log(out, "FileDrop: Drop target cleared from component.")
            } // end if: null parent
            else {
                DropTarget(c, dropListener)
                log(out, "FileDrop: Drop target added to component.")
            } // end else: parent not null
        } // end hierarchy listener
        if (c.parent != null) DropTarget(c, dropListener)
        if (recursive && c is Container) {
            // Get the container

            // Get it's components
            val comps = c.components

            // Set it's components as listeners also
            for (i in comps.indices) makeDropTarget(out, comps[i], recursive)
        } // end if: recursively set components as listener
    } // end dropListener

    /**
     * Determine if the dragged data is a file list.
     */
    private fun isDragOk(out: PrintStream?, evt: DropTargetDragEvent): Boolean {
        var ok = false

        // Get data flavors being dragged
        val flavors = evt.currentDataFlavors

        // See if any of the flavors are a file list
        var i = 0
        while (!ok && i < flavors.size) {
            // BEGIN 2007-09-12 Nathan Blomquist -- Linux (KDE/Gnome) support added.
            // Is the flavor a file list?
            val curFlavor = flavors[i]
            if (curFlavor.equals(DataFlavor.javaFileListFlavor) ||
                curFlavor.isRepresentationClassReader
            ) {
                ok = true
            }
            // END 2007-09-12 Nathan Blomquist -- Linux (KDE/Gnome) support added.
            i++
        } // end while: through flavors

        // If logging is enabled, show data flavors
        if (out != null) {
            if (flavors.size == 0) log(out, "FileDrop: no data flavors.")
            i = 0
            while (i < flavors.size) {
                log(out, flavors[i].toString())
                i++
            }
        } // end if: logging enabled
        return ok
    } // end isDragOk
    /* ********  I N N E R   I N T E R F A C E   L I S T E N E R  ******** */
    /**
     * Implement this inner interface to listen for when files are dropped. For example
     * your class declaration may begin like this:
     * `<pre>
     * public class MyClass implements FileDrop.Listener
     * ...
     * public void filesDropped( java.io.File[] files )
     * {
     * ...
     * }   // end filesDropped
     * ...
    </pre>` *
     *
     * @since 1.1
     */
    interface Listener {
        /**
         * This method is called when files have been successfully dropped.
         *
         * @param files An array of <tt>File</tt>s that were dropped.
         * @since 1.0
         */
        fun filesDropped(files: Array<File>, where: Point)
        fun dragEnter()
        fun dragLeave()
        fun dragOver(where: Point)
    } // end inner-interface Listener
    /* ********  I N N E R   C L A S S  ******** */
    /**
     * This is the event that is passed to the
     * [filesDropped(...)][FileDropListener.filesDropped] method in
     * your [FileDropListener] when files are dropped onto
     * a registered drop target.
     *
     *
     * I'm releasing this code into the Public Domain. Enjoy.
     *
     * @author Robert Harder
     * @author rob@iharder.net
     * @version 1.2
     */
    class Event
    /**
     * Constructs an [Event] with the array
     * of files that were dropped and the
     * [FileDrop] that initiated the event.
     *
     * @param files The array of files that were dropped
     * @source The event source
     * @since 1.1
     */ // end constructor
        (
        /**
         * Returns an array of files that were dropped on a
         * registered drop target.
         *
         * @return array of files that were dropped
         * @since 1.1
         */ // end getFiles
        val files: Array<File>, source: Any?
    ) : EventObject(source)  // end inner class Event
    /* ********  I N N E R   C L A S S  ******** */
    /**
     * At last an easy way to encapsulate your custom objects for dragging and dropping
     * in your Java programs!
     * When you need to create a [java.awt.datatransfer.Transferable] object,
     * use this class to wrap your object.
     * For example:
     * <pre>`
     * ...
     * MyCoolClass myObj = new MyCoolClass();
     * Transferable xfer = new TransferableObject( myObj );
     * ...
    `</pre> *
     * Or if you need to know when the data was actually dropped, like when you're
     * moving data out of a list, say, you can use the [TransferableObject.Fetcher]
     * inner class to return your object Just in Time.
     * For example:
     * <pre>`
     * ...
     * final MyCoolClass myObj = new MyCoolClass();
     *
     * TransferableObject.Fetcher fetcher = new TransferableObject.Fetcher()
     * {   public Object getObject(){ return myObj; }
     * }; // end fetcher
     *
     * Transferable xfer = new TransferableObject( fetcher );
     * ...
    `</pre> *
     *
     *
     * The [java.awt.datatransfer.DataFlavor] associated with
     * [TransferableObject] has the representation class
     * <tt>net.iharder.dnd.TransferableObject.class</tt> and MIME type
     * <tt>application/x-net.iharder.dnd.TransferableObject</tt>.
     * This data flavor is accessible via the static
     * [.DATA_FLAVOR] property.
     *
     *
     *
     * I'm releasing this code into the Public Domain. Enjoy.
     *
     * @author Robert Harder
     * @author rob@iharder.net
     * @version 1.2
     */
    class TransferableObject : Transferable {
        private var fetcher: Fetcher? = null
        private var data: Any? = null

        /**
         * Returns the custom [java.awt.datatransfer.DataFlavor] associated
         * with the encapsulated object or <tt>null</tt> if the [Fetcher]
         * constructor was used without passing a [java.lang.Class].
         *
         * @return The custom data flavor for the encapsulated object
         * @since 1.1
         */ // end getCustomDataFlavor
        var customDataFlavor: DataFlavor? = null
            private set

        /**
         * Creates a new [TransferableObject] that wraps <var>data</var>.
         * Along with the [.DATA_FLAVOR] associated with this class,
         * this creates a custom data flavor with a representation class
         * determined from `data.getClass()` and the MIME type
         * <tt>application/x-net.iharder.dnd.TransferableObject</tt>.
         *
         * @param data The data to transfer
         * @since 1.1
         */
        constructor(data: Any) {
            this.data = data
            customDataFlavor = DataFlavor(data.javaClass, MIME_TYPE)
        } // end constructor

        /**
         * Creates a new [TransferableObject] that will return the
         * object that is returned by <var>fetcher</var>.
         * No custom data flavor is set other than the default
         * [.DATA_FLAVOR].
         *
         * @param fetcher The [Fetcher] that will return the data object
         * @see Fetcher
         *
         * @since 1.1
         */
        constructor(fetcher: Fetcher?) {
            this.fetcher = fetcher
        } // end constructor

        /**
         * Creates a new [TransferableObject] that will return the
         * object that is returned by <var>fetcher</var>.
         * Along with the [.DATA_FLAVOR] associated with this class,
         * this creates a custom data flavor with a representation class <var>dataClass</var>
         * and the MIME type
         * <tt>application/x-net.iharder.dnd.TransferableObject</tt>.
         *
         * @param dataClass The [java.lang.Class] to use in the custom data flavor
         * @param fetcher   The [Fetcher] that will return the data object
         * @see Fetcher
         *
         * @since 1.1
         */
        constructor(dataClass: Class<*>?, fetcher: Fetcher?) {
            this.fetcher = fetcher
            customDataFlavor = DataFlavor(dataClass, MIME_TYPE)
        } // end constructor
        /* ********  T R A N S F E R A B L E   M E T H O D S  ******** */
        /**
         * Returns a two- or three-element array containing first
         * the custom data flavor, if one was created in the constructors,
         * second the default [.DATA_FLAVOR] associated with
         * [TransferableObject], and third the
         * [java.awt.datatransfer.DataFlavor.stringFlavor].
         *
         * @return An array of supported data flavors
         * @since 1.1
         */
        override fun getTransferDataFlavors(): Array<DataFlavor> {
            return if (customDataFlavor != null) arrayOf(
                customDataFlavor!!,
                DATA_FLAVOR,
                DataFlavor.stringFlavor
            ) // end flavors array
            else arrayOf(
                DATA_FLAVOR,
                DataFlavor.stringFlavor
            ) // end flavors array
        } // end getTransferDataFlavors

        /**
         * Returns the data encapsulated in this [TransferableObject].
         * If the [Fetcher] constructor was used, then this is when
         * the [getObject()][Fetcher.getObject] method will be called.
         * If the requested data flavor is not supported, then the
         * [getObject()][Fetcher.getObject] method will not be called.
         *
         * @param flavor The data flavor for the data to return
         * @return The dropped data
         * @since 1.1
         */
        @Throws(UnsupportedFlavorException::class, IOException::class)
        override fun getTransferData(flavor: DataFlavor): Any {
            // Native object
            if (flavor.equals(DATA_FLAVOR)) return (if (fetcher == null) data else fetcher!!.`object`)!!

            // String
            if (flavor.equals(DataFlavor.stringFlavor)) return if (fetcher == null) data.toString() else fetcher!!.`object`.toString()

            // We can't do anything else
            throw UnsupportedFlavorException(flavor)
        } // end getTransferData

        /**
         * Returns <tt>true</tt> if <var>flavor</var> is one of the supported
         * flavors. Flavors are supported using the `equals(...)` method.
         *
         * @param flavor The data flavor to check
         * @return Whether or not the flavor is supported
         * @since 1.1
         */
        override fun isDataFlavorSupported(flavor: DataFlavor): Boolean {
            // Native object
            if (flavor.equals(DATA_FLAVOR)) return true

            // String
            return if (flavor.equals(DataFlavor.stringFlavor)) true else false

            // We can't do anything else
        } // end isDataFlavorSupported
        /* ********  I N N E R   I N T E R F A C E   F E T C H E R  ******** */
        /**
         * Instead of passing your data directly to the [TransferableObject]
         * constructor, you may want to know exactly when your data was received
         * in case you need to remove it from its source (or do anyting else to it).
         * When the [getTransferData(...)][.getTransferData] method is called
         * on the [TransferableObject], the [Fetcher]'s
         * [getObject()][.getObject] method will be called.
         *
         * @author Robert Harder
         * @version 1.1
         * @copyright 2001
         * @since 1.1
         */
        interface Fetcher {
            /**
             * Return the object being encapsulated in the
             * [TransferableObject].
             *
             * @return The dropped object
             * @since 1.1
             */
            val `object`: Any
        } // end inner interface Fetcher

        companion object {
            /**
             * The MIME type for [.DATA_FLAVOR] is
             * <tt>application/x-net.iharder.dnd.TransferableObject</tt>.
             *
             * @since 1.1
             */
            const val MIME_TYPE = "application/x-net.iharder.dnd.TransferableObject"

            /**
             * The default [java.awt.datatransfer.DataFlavor] for
             * [TransferableObject] has the representation class
             * <tt>net.iharder.dnd.TransferableObject.class</tt>
             * and the MIME type
             * <tt>application/x-net.iharder.dnd.TransferableObject</tt>.
             *
             * @since 1.1
             */
            val DATA_FLAVOR = DataFlavor(TransferableObject::class.java, MIME_TYPE)
        }
    } // end class TransferableObject

    companion object {
        /**
         * Discover if the running JVM is modern enough to have drag and drop.
         */
        private var supportsDnD: Boolean? = null
        private fun supportsDnD(): Boolean {   // Static Boolean
            if (supportsDnD == null) {
                var support = false
                support = try {
                    val arbitraryDndClass = Class.forName("java.awt.dnd.DnDConstants")
                    true
                } // end try
                catch (e: Exception) {
                    false
                } // end catch
                supportsDnD = support
            } // end if: first time through
            return supportsDnD!!
        } // end supportsDnD

        // BEGIN 2007-09-12 Nathan Blomquist -- Linux (KDE/Gnome) support added.
        private const val ZERO_CHAR_STRING = "" + 0.toChar()
        private fun createFileArray(bReader: BufferedReader, out: PrintStream?): Array<File> {
            try {
                val list: MutableList<File> = mutableListOf()
                var line: String? = null
                while (bReader.readLine().also { line = it } != null) {
                    try {
                        // kde seems to append a 0 char to the end of the reader
                        if (ZERO_CHAR_STRING == line) continue
                        val file = File(URI(line))
                        list.add(file)
                    } catch (ex: Exception) {
                        log(out, "Error with " + line + ": " + ex.message)
                    }
                }
                return list.toTypedArray()
            } catch (ex: IOException) {
                log(out, "FileDrop: IOException")
            }
            return arrayOf()
        }

        /**
         * Outputs <tt>message</tt> to <tt>out</tt> if it's not null.
         */
        private fun log(out: PrintStream?, message: String) {   // Log message if requested
            out?.println(message)
        } // end log

        /**
         * Removes the drag-and-drop hooks from the component and optionally
         * from the all children. You should call this if you add and remove
         * components after you've set up the drag-and-drop.
         * This will recursively unregister all components contained within
         * <var>c</var> if <var>c</var> is a [java.awt.Container].
         *
         * @param c The component to unregister as a drop target
         * @since 1.0
         */
        fun remove(c: Component): Boolean {
            return remove(null, c, true)
        } // end remove

        /**
         * Removes the drag-and-drop hooks from the component and optionally
         * from the all children. You should call this if you add and remove
         * components after you've set up the drag-and-drop.
         *
         * @param out       Optional [java.io.PrintStream] for logging drag and drop messages
         * @param c         The component to unregister
         * @param recursive Recursively unregister components within a container
         * @since 1.0
         */
        fun remove(out: PrintStream?, c: Component, recursive: Boolean): Boolean {   // Make sure we support dnd.
            return if (supportsDnD()) {
                log(out, "FileDrop: Removing drag-and-drop hooks.")
                c.dropTarget = null
                if (recursive && c is Container) {
                    val comps = c.components
                    for (i in comps.indices) remove(out, comps[i], recursive)
                    true
                } // end if: recursive
                else false
            } // end if: supports DnD
            else false
        } // end remove
    }
} // end class FileDrop
