In the context of our research study, please record the three most important decisions for each sprint. For this purpose, the sprint will be extended according to the following graphic. Details can be found [in the study manual](https://git.informatik.tu-cottbus.de/schubmat/markdown-decision-records).

![integration into Scrum sprint](https://git.informatik.tu-cottbus.de/schubmat/markdown-decision-records/raw/master/misc/img/ScrumProcessIntegration.png)


## Actions following the sprint planning

* At the end of the sprint planning, the team selects the three to five most important decisions.
 * Optional: An explicit vote ([see study manual](https://git.informatik.tu-cottbus.de/schubmat/markdown-decision-records) + figure below)
* A selected team member transfers the decisions to MDR files and places them on Gitlab.

## Sprint End Actions

* Check whether the documented decisions are the really important ones in this sprint and whether the documentation needs to be updated. 
* If necessary, update the documentation.
* A selected team member prepares the decisions and places the MDR files on Gitlab.

## Markdown Decision Record -- Templates

To ease the documentation of decision during the _Softwarepraktikum_ / _Softwaretechnikpraktikum_ we provide you with a set of three templates. Each decision is to be placed in a separate copy of one of the provided templates. Finally, the documentation should be uploaded in _GitLab_ to a folder called `doc/mdr`, where it is easily accessible to the whole team. The links to the templates are as follows:
* [Compact template](https://git.informatik.tu-cottbus.de/schubmat/markdown-decision-records/raw/master/templates/captureTemplate_compact.md) (captures the minimum of required information)
* [Extensive template](https://git.informatik.tu-cottbus.de/schubmat/markdown-decision-records/raw/master/templates/captureTemplate_extensive.md) (captures an elaborate record of rationale information)
* [Table-based template](https://git.informatik.tu-cottbus.de/schubmat/markdown-decision-records/raw/master/templates/captureTemplate_table.md) (captures a structured overview of rationale information)

Therefore, at least three files should be created per sprint, which represent the most important decisions.

## Markdown Decision Record -- File Name Conventions

Please use the following naming scheme for your MDRs (explained by three examples and corresponding elaboration) and store them in the `doc/mdr/` project folder:

Examples:

  * `2017-01-01 Choose a database.md`
  * `2017-01-02 Handle more users.md`
  * `2017-01-03 Improve application security.md`

Naming scheme:

  * The name has the date as YYYY-MM-DD. This arrangement is ISO standard, and is useful for easily sorting by date. 
  * The name has a present tense imperative verb phrase. This is helpful for readability and matches a commit message format.
  * The name uses sentence capitalization and spaces. This is helpful for readability.
  * The extension is markdown. This can be useful for easy formatting.

## Misc 

If a vote on the most important decisions is really necessary, then it should take place in turn and without discussion, in order to make things as efficient as possible.

![SelectionProcess.svg](https://git.informatik.tu-cottbus.de/schubmat/markdown-decision-records/raw/master/misc/img/SelectionProcess.png)