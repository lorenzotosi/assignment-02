import lib.ReactiveDependencyAnalyser.ReactiveDependencyAnalyser
import lib.{ClassDepsReport, PackageDepsReport, ProjectDepsReport}

import scala.swing._
import scala.swing.event._
import java.io.File
import scala.jdk.CollectionConverters._
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.core.Observable
import java.util.concurrent.atomic.AtomicInteger
import javax.swing.SwingUtilities

object DepsGui:

	// Custom events for communicating dynamic data to the GUI
	case class UpdateClassCount(count: Int) extends Event
	case class UpdateDependencyCount(count: Int) extends Event
	case class UpdateStatus(message: String) extends Event
	case class AnalysisStarted() extends Event
	case class AnalysisCompleted() extends Event

	def createGui(): Frame = new MainFrame {
		title = "Dependency Analyser"

		// Create the analyzer
		val analyzer = new ReactiveDependencyAnalyser()

		// Track analysis state
		var currentAnalysis: Option[Disposable] = None
		val classCounter = new AtomicInteger(0)
		val dependencyCounter = new AtomicInteger(0)

		// Folder selection components
		val folderChooser = new FileChooser(new File("."))
		folderChooser.fileSelectionMode = FileChooser.SelectionMode.DirectoriesOnly

		val folderLabel = new Label("Source Folder: ")
		val folderField = new TextField {
			columns = 30
			editable = false
		}
		val selectFolderButton = new Button("Browse")

		val startButton = new Button("Start Analysis") {
			enabled = false  // Disabled until a folder is selected
		}

		val classCountLabel = new Label("Classes/Interfaces: 0")
		val dependencyCountLabel = new Label("Dependencies: 0")
		val statusBox = new TextArea {
			editable = false
			rows = 10
			lineWrap = true
			wordWrap = true
		}

		// Placeholder for dependency graph
		val graphPanel = new ScrollPane(new Label("Dependency Graph will appear here...")) {
			preferredSize = new Dimension(600, 400)
		}

		// Layout
		contents = new BorderPanel {
			layout(new BoxPanel(Orientation.Vertical) {
				contents += new FlowPanel(folderLabel, folderField, selectFolderButton)
				contents += new FlowPanel(startButton)
				contents += new FlowPanel(classCountLabel, dependencyCountLabel)
				contents += new ScrollPane(statusBox)
				contents += graphPanel
				border = Swing.EmptyBorder(10, 10, 10, 10)
			}) = BorderPanel.Position.Center
		}

		// Helper methods to update UI from any thread
		def updateStatus(message: String): Unit = {
			SwingUtilities.invokeLater(() => statusBox.append(s"${message}\n"))
		}

		def updateClassCount(count: Int): Unit = {
			SwingUtilities.invokeLater(() => classCountLabel.text = s"Classes/Interfaces: $count")
		}

		def updateDependencyCount(count: Int): Unit = {
			SwingUtilities.invokeLater(() => dependencyCountLabel.text = s"Dependencies: $count")
		}

		def resetCounters(): Unit = {
			classCounter.set(0)
			dependencyCounter.set(0)
			updateClassCount(0)
			updateDependencyCount(0)
		}

		def enableControls(enabled: Boolean): Unit = {
			SwingUtilities.invokeLater(() => {
				startButton.enabled = enabled
				selectFolderButton.enabled = enabled
			})
		}

		// Reaction handlers
		listenTo(selectFolderButton, startButton)
		reactions += {
			case ButtonClicked(`selectFolderButton`) =>
				val result = folderChooser.showOpenDialog(null)
				if (result == FileChooser.Result.Approve) {
					folderField.text = folderChooser.selectedFile.getAbsolutePath
					startButton.enabled = true
				}

			case ButtonClicked(`startButton`) if folderField.text.nonEmpty =>
				// Cancel any ongoing analysis
				currentAnalysis.foreach(_.dispose())

				// Reset UI
				statusBox.text = ""
				resetCounters()
				enableControls(false)

				val projectFolder = new File(folderField.text)
				updateStatus(s"Starting analysis of project: ${projectFolder.getName}")

				// Start the analysis
				currentAnalysis = Some(
					analyzer.getProjectDependencies(projectFolder)
						.subscribeOn(Schedulers.io())
						.subscribe(
							// onSuccess
							projectReport => {
								// Count classes
								val classesCount = countClasses(projectReport)
								// Count dependencies
								val depsCount = projectReport.depsList.size

								updateClassCount(classesCount)
								updateDependencyCount(depsCount)
								updateStatus(s"Analysis completed. Found $classesCount classes and $depsCount dependencies.")

								// Here you would update the graph visualization
								// For now we just log the structure
								updateStatus("Project structure:")
								projectReport.packages.foreach(pkg => {
									updateStatus(s"  Package: ${pkg.packageName.getName}")
									pkg.classes.foreach(cls => {
										updateStatus(s"    Class: ${cls.file.getName}")
									})
								})

								enableControls(true)
							},
							// onError
							error => {
								updateStatus(s"Error during analysis: ${error.getMessage}")
								error.printStackTrace()
								enableControls(true)
							}
						)
				)
		}

		// Helper method to count classes in a project report
		def countClasses(report: ProjectDepsReport): Int = {
			report.packages.map(pkg => countClassesInPackage(pkg)).sum
		}

		def countClassesInPackage(report: PackageDepsReport): Int = {
			report.classes.size + report.subPackages.map(countClassesInPackage).sum
		}
	}