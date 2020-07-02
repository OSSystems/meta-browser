import os
import bb
from   bb    import data
from   bb.fetch2 import FetchMethod
from   bb.fetch2 import runfetchcmd
from   bb.fetch2 import logger

class Gclient(FetchMethod):
    """Class for sync code from depot-based repositories"""
    def init(self, d):
        pass

    def supports(self, ud, d):
        """
        Check to see if a given url starts with "depot" or "gclient".
        """
        return ud.type in ["depot", "gclient"]

    def urldata_init(self, ud, d):
        """
        supported options:
            name: package name
            jobs: number of parallel jobs
            hash: Hash to checkout
            tag: Tag to checkout
            branch: Branch to checkout
        """
        ud.name = ud.parm.get('name', '')
        ud.njobs = ud.parm.get('jobs', '1')
        ud.packname = "gclient_%s%s_%s" % (ud.host, ud.path.replace("/", "."), ud.name)
        ud.localfile = data.expand("%s.tar.gz" % ud.packname, d)
        ud.hash = ud.parm.get('hash','')
        ud.tag = ud.parm.get('tag','')
        ud.branch = ud.parm.get('branch', 'origin/master')
        ud.download_path = ud.parm.get('download_path','')

    def download(self, ud, d):
        """
        do fetch
        """

        if os.access(os.path.join(d.getVar("DL_DIR"), ud.localfile), os.R_OK):
            logger.debug(1, "%s already exists (or was stashed). Skipping gclient sync.", ud.localpath)
            return

        depot_dir = d.getVar("DEPOTDIR") or os.path.join(d.getVar("DL_DIR"), "depot")
        sync_dir = os.path.join(depot_dir, ud.packname)

        bb.utils.mkdirhier(sync_dir)
        os.chdir(sync_dir)

        if not os.path.exists(os.path.join(sync_dir, ".gclient")):
            logger.info('This is the first time to sync this depot, config it as http://%s%s'
                    % (ud.host, ud.path))
            if (ud.download_path):
                runfetchcmd('gclient config --name "%s" --unmanaged http://%s%s' % (ud.download_path, ud.host, ud.path), d)
            else:
                runfetchcmd('gclient config --unmanaged http://%s%s' % (ud.download_path, ud.host, ud.path), d)
        runfetchcmd('gclient sync' , d)
        logger.info('goto src dir')
        logger.info(os.path.join(sync_dir,ud.download_path))
        if (ud.download_path):
            os.chdir(os.path.join(sync_dir,ud.download_path))
        logger.info('Checkout specific tag')
        runfetchcmd('git fetch --all --tags', d)
        runfetchcmd('git checkout tags/%s' % ud.tag, d)

        logger.info('Sync subprojects')
        runfetchcmd('gclient sync --with_branch_heads --with_tags --jobs %s' % ud.njobs, d)
        os.chdir(sync_dir)

        logger.info('Creating tarball %s.' % ud.localfile)
        runfetchcmd('tar --exclude .svn --exclude .git -czf %s ./' %
                os.path.join(d.getVar("DL_DIR"), ud.localfile), d)

    def _build_revision(self, url, ud, d):
        return None

