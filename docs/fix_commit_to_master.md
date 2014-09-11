## Fix accidental commit to master

### Example bad commit

```
$ git log
commit 504bb330f21b1895ec0b4b3aa4467bc8b2da3517
Author: Matthew Sykes and Zach Robinson <pair+matthew+zrobinson@pivotallabs.com>
Date:   Thu Sep 11 12:08:41 2014 -0700

    commit to fix
```

### 1. Revert the accidental commits to master

```
$ git revert 504bb330f2
[master 36418aa] Revert "commit to fix"
 1 file changed, 0 insertions(+), 0 deletions(-)
 create mode 100644 bad-file
	
$ git log
commit 36418aa5530b958a5aebbb68a3981aaf5f3f8ea8
Author: Matthew Sykes and Zach Robinson <pair+matthew+zrobinson@pivotallabs.com>
Date:   Thu Sep 11 12:10:04 2014 -0700

    Revert "commit to fix"

    This reverts commit 504bb330f21b1895ec0b4b3aa4467bc8b2da3517.

commit 504bb330f21b1895ec0b4b3aa4467bc8b2da3517
Author: Matthew Sykes and Zach Robinson <pair+matthew+zrobinson@pivotallabs.com>
Date:   Thu Sep 11 12:08:41 2014 -0700

    commit to fix

```

### 2. Merge master to develop so the initial and revert commit show up on develop.

```
$ git checkout develop
Switched to branch 'develop'
Your branch is up-to-date with 'origin/develop'.

$ git merge master
Already up-to-date!
Merge made by the 'recursive' strategy.

$ git log
commit 47cc979340696dffa8a5abd6c1b652832b96628c
Merge: aec4251 36418aa
Author: Matthew Sykes and Zach Robinson <pair+matthew+zrobinson@pivotallabs.com>
Date:   Thu Sep 11 12:10:52 2014 -0700

    Merge branch 'master' into develop

commit 36418aa5530b958a5aebbb68a3981aaf5f3f8ea8
Author: Matthew Sykes and Zach Robinson <pair+matthew+zrobinson@pivotallabs.com>
Date:   Thu Sep 11 12:10:04 2014 -0700

    Revert "commit to fix"

    This reverts commit 504bb330f21b1895ec0b4b3aa4467bc8b2da3517.

commit 504bb330f21b1895ec0b4b3aa4467bc8b2da3517
Author: Matthew Sykes and Zach Robinson <pair+matthew+zrobinson@pivotallabs.com>
Date:   Thu Sep 11 12:08:41 2014 -0700

    commit to fix
```

### 3. On develop, revert the revert commit. The intended change is now on develop.

```
$ git revert 36418aa5530b95
[develop 350b69a] Revert "Revert "commit to fix""
 1 file changed, 0 insertions(+), 0 deletions(-)
 create mode 100644 bad-file

$ git log 
commit 350b69a103aa8de6e5a2c2d03086b729c4c40add
Author: Matthew Sykes and Zach Robinson <pair+matthew+zrobinson@pivotallabs.com>
Date:   Thu Sep 11 12:11:54 2014 -0700

    Revert "Revert "commit to fix""

    This reverts commit 36418aa5530b958a5aebbb68a3981aaf5f3f8ea8.

commit 47cc979340696dffa8a5abd6c1b652832b96628c
Merge: aec4251 36418aa
Author: Matthew Sykes and Zach Robinson <pair+matthew+zrobinson@pivotallabs.com>
Date:   Thu Sep 11 12:10:52 2014 -0700

    Merge branch 'master' into develop

commit 36418aa5530b958a5aebbb68a3981aaf5f3f8ea8
Author: Matthew Sykes and Zach Robinson <pair+matthew+zrobinson@pivotallabs.com>
Date:   Thu Sep 11 12:10:04 2014 -0700

    Revert "commit to fix"

    This reverts commit 504bb330f21b1895ec0b4b3aa4467bc8b2da3517.

commit 504bb330f21b1895ec0b4b3aa4467bc8b2da3517
Author: Matthew Sykes and Zach Robinson <pair+matthew+zrobinson@pivotallabs.com>
Date:   Thu Sep 11 12:08:41 2014 -0700

    commit to fix
```
