package com.chama.groupmoneymanager // Or your correct package name

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp // <-- This is the most important part. Is it here?
class GroupMoneyManagerApp : Application()