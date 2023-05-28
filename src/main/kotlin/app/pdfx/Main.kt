package app.pdfx

import app.pdfx.CommandLine.Companion.parse
import app.pdfx.CommandLine.ParseError
import app.pdfx.FileList.fileList
import app.pdfx.prefs.FilePreferencesFactory
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import java.io.File
import java.io.FileWriter
import java.io.PrintWriter
import java.util.concurrent.*
import java.util.prefs.Preferences
import javax.swing.JFrame
import javax.swing.SwingWorker

object Main {
    internal var batchGuiCounter = 0
    val batchGuiCommand: String
        get() = "batch-gui-" + batchGuiCounter++
    var cmdQueue: BlockingQueue<CommandLine?> = LinkedBlockingDeque()

    // this must be swing worker
    fun makeBatchWindow(commandName: String?, command: CommandDescription?, fileList: List<String?>?) {
        logLine("makeBatchWindow", commandName)
        val bs = BatchOperationWindow(command)
        bs.appendFiles(fileList(fileList!!))
        bs.defaultCloseOperation = JFrame.DISPOSE_ON_CLOSE
        bs.addWindowListener(object : WindowAdapter() {
            override fun windowClosing(winEvt: WindowEvent) {
                batchInstances.remove(commandName)
                maybeExit()
            }
        })
        batchInstances[commandName] = bs
        bs.isVisible = true
    }

    internal fun executeCommandSwingWorker(cmdLine: CommandLine) {
        logLine("executeCommandSwingWorker", cmdLine.toString())
        if (cmdLine.hasCommand()) {
            try {
                val bs = batchInstances[cmdLine.command!!.name]
                if (bs != null) {
                    bs.appendFiles(fileList(cmdLine.fileList))
                } else {
                    makeBatchWindow(cmdLine.command!!.name, cmdLine.command, cmdLine.fileList)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return
        }
        if (cmdLine.batchGui) {
            makeBatchWindow(batchGuiCommand, null, cmdLine.fileList)
            return
        }
        val files: MutableList<String?> = ArrayList(cmdLine.fileList)
        if (files.size == 0) {
            files.add(null)
        }
        for (file in files) {
            try {
                val fileAbsPath = if (file != null) File(file).absolutePath else null
                // If we have file, and a single open empty window, load the file in it
                if (fileAbsPath != null && editorInstances.size == 1 && editorInstances[0].currentFile == null) {
                    editorInstances[0].loadFile(fileAbsPath)
                    return
                }
                logLine("executeCommand fileName", fileAbsPath)
                for (window in editorInstances) {
                    val wFile = window.currentFile
                    logLine("check ", wFile?.absolutePath)
                    if (fileAbsPath == null && wFile == null || wFile != null && wFile.absolutePath == fileAbsPath) {
                        logLine("match", null)
                        if (window.state == JFrame.ICONIFIED) {
                            window.state = JFrame.NORMAL
                        }
                        window.toFront()
                        window.repaint()
                        window.reloadFile()
                        return
                    }
                }
                logLine("open editor", file)
                val window = PDFMetadataEditWindow(file)
                window.defaultCloseOperation = JFrame.DISPOSE_ON_CLOSE
                window.addWindowListener(object : WindowAdapter() {
                    override fun windowClosing(winEvt: WindowEvent) {
                        editorInstances.remove(window)
                        maybeExit()
                    }
                })
                editorInstances.add(window)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    @JvmStatic
    fun executeCommand(cmdLine: CommandLine?) {
        logLine("executeCommand:", cmdLine.toString())
        try {
            cmdQueue.put(cmdLine)
        } catch (e: InterruptedException) {
            // TODO Auto-generated catch block
            e.printStackTrace()
        }
    }

    val debugLog = System.getProperty("debugLog")
    internal fun logLine(context: String, line: String?) {
        if (debugLog == null) {
            return
        }
        println("$context:: $line")
        try {
            val output = PrintWriter(
                FileWriter(
                    System.getProperty("java.io.tmpdir") + File.separator + "pdf-metada-editor-log.txt",
                    true
                )
            )
            output.printf("%s:: %s\r\n", context, line ?: "null")
            output.close()
        } catch (e: Exception) {
        }
    }

    var batchInstances: MutableMap<String?, BatchOperationWindow> = HashMap()
    var editorInstances: MutableList<PDFMetadataEditWindow> = ArrayList()
    fun maybeExit() {
        if (batchInstances.size == 0 && editorInstances.size == 0 && cmdQueue.size == 0) {
            System.exit(0)
        }
    }

    fun numWindows(): Int {
        return batchInstances.size + editorInstances.size
    }

    @JvmStatic
    fun main(args: Array<String>) {
        var cmdLine: CommandLine? = null
        try {
            cmdLine = parse(args)
        } catch (e: ParseError) {
            logLine("ParseError", e.toString())
            System.err.println(e)
            return
        }
        //System.out.println(cmdLine);
        if (cmdLine!!.noGui) {
            MainCli.main(cmdLine)
            return
        }
        //	    try {
//    	UIManager.setLookAndFeel(
//    			UIManager.getCrossPlatformLookAndFeelClassName());
//    } 
//    catch (UnsupportedLookAndFeelException e) {}
//    catch (ClassNotFoundException e) {}
//    catch (InstantiationException e) {}
//    catch (IllegalAccessException e) {}
        executeCommand(cmdLine)
        logLine("DDE:", "DONE")
        val commandsExecutor = CommandsExecutor()
        commandsExecutor.execute()

        // Wait for at least on windows to open up, or the program
        // terminates without showing anything
        try {
            while (numWindows() == 0) {
                try {
                    commandsExecutor[50, TimeUnit.MILLISECONDS]
                } catch (e: TimeoutException) {
                }
            }
        } catch (e: InterruptedException) {
            e.printStackTrace()
        } catch (e: ExecutionException) {
            e.printStackTrace()
        }
    }

    var _prefs: Preferences? = null
    @JvmStatic
    val preferences: Preferences?
        get() {
            if (_prefs == null) {
                System.setProperty("java.util.prefs.PreferencesFactory", FilePreferencesFactory::class.java.name)
                _prefs = Preferences.userRoot().node("pdfxMetadataEditor")
            }
            return _prefs
        }

    internal class CommandsExecutor : SwingWorker<Void, CommandLine>() {
        public override fun doInBackground(): Void {
            while (true) {
                var cmdLine: CommandLine
                try {
                    cmdLine = cmdQueue.take()
                    logLine("publish", cmdLine.toString())
                    publish(cmdLine)
                } catch (e: InterruptedException) {
                    // TODO Auto-generated catch block
                    e.printStackTrace()
                }
            }
        }

        override fun process(chunks: List<CommandLine>) {
            for (cmdLine in chunks) {
                executeCommandSwingWorker(cmdLine)
            }
        }
    }
}
