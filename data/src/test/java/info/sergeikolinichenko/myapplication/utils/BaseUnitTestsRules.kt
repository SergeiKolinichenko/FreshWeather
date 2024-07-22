package info.sergeikolinichenko.myapplication.utils

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Rule
import org.junit.runner.RunWith

/** Created by Sergei Kolinichenko on 09.05.2024 at 20:54 (GMT+3) **/
@RunWith(AndroidJUnit4::class)
abstract class BaseUnitTestsRules {
  @OptIn(ExperimentalCoroutinesApi::class)
  @get:Rule
  val coroutineTestRule = MainCoroutineScopeRule()

  @get:Rule
  val instantTaskExecutorRule = InstantTaskExecutorRule()
}