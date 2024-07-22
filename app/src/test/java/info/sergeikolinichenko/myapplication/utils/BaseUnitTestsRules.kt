package info.sergeikolinichenko.myapplication.utils

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import info.sergeikolinichenko.myapplication.MainCoroutineScopeRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import org.junit.Rule
import org.junit.runner.RunWith

/** Created by Sergei Kolinichenko on 09.05.2024 at 20:54 (GMT+3) **/

@ExperimentalCoroutinesApi
abstract class BaseUnitTestsRules(dispatcher: TestDispatcher = UnconfinedTestDispatcher()) {

  @get:Rule
  val coroutineTestRule = MainCoroutineScopeRule(dispatcher = dispatcher)

  @get:Rule
  val instantTaskExecutorRule = InstantTaskExecutorRule()
}