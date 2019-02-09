package com.devops.jenkins

class Git {


    def getChangesSinceLastBuild() {
        def changes = []
        def changeLogSets = currentBuild.rawBuild.changeSets
        for (int i = 0; i < changeLogSets.size(); i++) {
            def entries = changeLogSets[i].items
            for (int j = 0; j < entries.length; j++) {
                def entry = serializeGitChangeSet(entries[j])
                changes.add(entry)
            }
        }
        return changes
    }

    @NonCPS
    def serializeGitChangeSet(hudson.plugins.git.GitChangeSet entry) {
        def serialized = [
                commitId: entry.commitId,
                author: entry.author.toString(),
                authorEmail: entry.authorEmail,
                timestamp: entry.timestamp,
                date: new Date(entry.timestamp).toString(),
                msg: entry.msg,
                comment: entry.comment
        ]

        def files = new ArrayList(entry.affectedFiles)
        def filesSerialized = []
        for (int k = 0; k < files.size(); k++) {
            def file = files[k]
            def fileSerialized = [
                    editType: file.editType.name,
                    path: file.path
            ]
            filesSerialized.add(fileSerialized)
        }
        serialized.put("files", filesSerialized)

        return serialized
    }

}
