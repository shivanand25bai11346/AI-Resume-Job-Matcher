/**
 * JobMatch AI — Frontend Application Logic
 * Integrates Editorial Two-Card Hero, Presets, Apache PDFBox Parsing, Matching Engine & Gap Roadmaps
 */

document.addEventListener('DOMContentLoaded', () => {
  // State
  let activeTab = 'pdf'; // 'pdf' or 'text'
  let selectedPdfFile = null;
  let presetsData = [];

  // DOM Elements — Workspace & Inputs
  const tabPdf = document.getElementById('tab-pdf');
  const tabText = document.getElementById('tab-text');
  const panePdf = document.getElementById('pane-pdf');
  const paneText = document.getElementById('pane-text');

  const dropZone = document.getElementById('drop-zone');
  const pdfFileInput = document.getElementById('pdf-file-input');
  const dropZoneContent = document.getElementById('drop-zone-content');
  const filePreviewCard = document.getElementById('file-preview-card');
  const selectedFileName = document.getElementById('selected-file-name');
  const selectedFileSize = document.getElementById('selected-file-size');
  const removeFileBtn = document.getElementById('remove-file-btn');
  const extractedPreviewWrapper = document.getElementById('extracted-preview-wrapper');
  const extractedPreviewText = document.getElementById('extracted-preview-text');
  const extractedWordCount = document.getElementById('extracted-word-count');

  const candidateNameInput = document.getElementById('candidate-name-input');
  const resumeTextInput = document.getElementById('resume-text-input');
  const resumeCharCount = document.getElementById('resume-char-count');
  const clearResumeBtn = document.getElementById('clear-resume-btn');

  const jobTitleInput = document.getElementById('job-title-input');
  const companyInput = document.getElementById('company-input');
  const jobDescInput = document.getElementById('job-desc-input');
  const jobCharCount = document.getElementById('job-char-count');
  const clearJobBtn = document.getElementById('clear-job-btn');

  const analyzeBtn = document.getElementById('analyze-btn');
  const analyzeSpinner = document.getElementById('analyze-spinner');
  const resetAllBtn = document.getElementById('reset-all-btn');
  const presetContainer = document.getElementById('preset-container');

  const resultsSection = document.getElementById('results-section');
  const scorePercentVal = document.getElementById('score-percent-val');
  const gaugeFillCircle = document.getElementById('gauge-fill-circle');
  const verdictBadge = document.getElementById('verdict-badge');
  const verdictDesc = document.getElementById('verdict-desc');
  const resultsTargetInfo = document.getElementById('results-target-info');

  const metricSkillVal = document.getElementById('metric-skill-val');
  const metricSkillSub = document.getElementById('metric-skill-sub');
  const barSkill = document.getElementById('bar-skill');

  const metricTfidfVal = document.getElementById('metric-tfidf-val');
  const barTfidf = document.getElementById('bar-tfidf');

  const metricSemanticVal = document.getElementById('metric-semantic-val');
  const barSemantic = document.getElementById('bar-semantic');

  const countMatched = document.getElementById('count-matched');
  const countMissing = document.getElementById('count-missing');
  const countExtra = document.getElementById('count-extra');

  const matchedSkillsContainer = document.getElementById('matched-skills-container');
  const missingSkillsContainer = document.getElementById('missing-skills-container');
  const extraSkillsCard = document.getElementById('extra-skills-card');
  const extraSkillsContainer = document.getElementById('extra-skills-container');
  const roadmapTimeline = document.getElementById('roadmap-timeline');

  // Navigation, Top Bar, & Drawers
  const announcementBar = document.getElementById('announcement-bar');
  const announcementCloseBtn = document.getElementById('announcement-close-btn');

  const mobileMenuToggle = document.getElementById('mobile-menu-toggle');
  const mobileNavDrawer = document.getElementById('mobile-nav-drawer');

  const viewHistoryBtn = document.getElementById('view-history-btn');
  const mobileHistoryBtn = document.getElementById('mobile-history-btn');
  const closeDrawerBtn = document.getElementById('close-drawer-btn');
  const historyDrawerOverlay = document.getElementById('history-drawer-overlay');
  const historyListContainer = document.getElementById('history-list-container');
  const printReportBtn = document.getElementById('print-report-btn');

  // Hero Card Elements
  const heroAnalyzeCta = document.getElementById('hero-analyze-cta');
  const heroMatchCta = document.getElementById('hero-match-cta');
  const heroTypingPrompt = document.getElementById('hero-typing-prompt');
  const mosaicTiles = document.querySelectorAll('.mosaic-tile');

  // Auth Modal Elements
  const authModal = document.getElementById('auth-modal');
  const authModalTitle = document.getElementById('auth-modal-title');
  const headerLoginBtn = document.getElementById('header-login-btn');
  const headerGetStartedBtn = document.getElementById('header-get-started-btn');
  const mobileLoginBtn = document.getElementById('mobile-login-btn');
  const mobileGetStartedBtn = document.getElementById('mobile-get-started-btn');
  const closeAuthBtn = document.getElementById('close-auth-btn');
  const modalTabRegister = document.getElementById('modal-tab-register');
  const modalTabLogin = document.getElementById('modal-tab-login');
  const authForm = document.getElementById('auth-form');

  // -------------------------------------------------------------
  // 1. Initialization & Presets
  // -------------------------------------------------------------
  loadPresets();
  initTypingAnimation();
  initHeaderInteractions();
  initHeroInteractions();

  async function loadPresets() {
    try {
      const res = await fetch('/api/jobs/presets');
      if (!res.ok) throw new Error('Failed to load presets');
      presetsData = await res.json();

      presetContainer.innerHTML = '';
      presetsData.forEach((preset, index) => {
        const btn = document.createElement('button');
        btn.type = 'button';
        btn.className = 'preset-btn';
        btn.textContent = preset.title;
        btn.addEventListener('click', () => applyPreset(preset, btn));
        presetContainer.appendChild(btn);

        // Pre-select first preset by default for instant delight
        if (index === 0) {
          applyPreset(preset, btn);
        }
      });
    } catch (e) {
      console.warn('Could not load presets:', e);
      presetContainer.innerHTML = '<span style="font-size: 12px; color: #64748b;">Ready to accept custom inputs</span>';
    }
  }

  function applyPreset(preset, activeBtn) {
    document.querySelectorAll('.preset-btn').forEach(b => b.classList.remove('active'));
    if (activeBtn) activeBtn.classList.add('active');

    // Switch to Text tab to show preloaded content
    switchTab('text');

    jobTitleInput.value = preset.title || '';
    companyInput.value = preset.company || '';
    jobDescInput.value = preset.description || '';
    updateCharCount(jobDescInput, jobCharCount);

    candidateNameInput.value = preset.sampleCandidateName || '';
    resumeTextInput.value = preset.sampleResumeText || '';
    updateCharCount(resumeTextInput, resumeCharCount);
  }

  // -------------------------------------------------------------
  // 2. Hero Section Interactions & Animated AI Typing
  // -------------------------------------------------------------
  function initTypingAnimation() {
    if (!heroTypingPrompt) return;

    const phrases = [
      "What role or resume would you like to match?",
      "Senior Java Backend Engineer with Spring Boot 3.4...",
      "Extracting Apache PDFBox text & NLP skill vectors...",
      "Cloud Architect with AWS, Kubernetes & Microservices...",
      "Calculating ATS compatibility score & skill gap roadmap..."
    ];

    let phraseIdx = 0;
    let charIdx = 0;
    let isDeleting = false;
    let typingSpeed = 50;

    function type() {
      const currentPhrase = phrases[phraseIdx];

      if (isDeleting) {
        heroTypingPrompt.textContent = currentPhrase.substring(0, charIdx - 1);
        charIdx--;
        typingSpeed = 25;
      } else {
        heroTypingPrompt.textContent = currentPhrase.substring(0, charIdx + 1);
        charIdx++;
        typingSpeed = 55;
      }

      if (!isDeleting && charIdx === currentPhrase.length) {
        isDeleting = true;
        typingSpeed = 2200; // Pause at end of sentence
      } else if (isDeleting && charIdx === 0) {
        isDeleting = false;
        phraseIdx = (phraseIdx + 1) % phrases.length;
        typingSpeed = 400; // Pause before new sentence
      }

      setTimeout(type, typingSpeed);
    }

    type();
  }

  function initHeroInteractions() {
    // Card 1 CTA: Analyze Resume -> scroll to resume input and trigger tab
    if (heroAnalyzeCta) {
      heroAnalyzeCta.addEventListener('click', (e) => {
        e.preventDefault();
        const target = document.getElementById('resume-input-container') || document.getElementById('workspace');
        if (target) {
          target.scrollIntoView({ behavior: 'smooth', block: 'start' });
        }
        switchTab('pdf');
      });
    }

    // Card 2 CTA: Find Your Match -> scroll to job input
    if (heroMatchCta) {
      heroMatchCta.addEventListener('click', (e) => {
        e.preventDefault();
        const target = document.getElementById('job-input-container') || document.getElementById('workspace');
        if (target) {
          target.scrollIntoView({ behavior: 'smooth', block: 'start' });
          setTimeout(() => jobDescInput.focus(), 600);
        }
      });
    }

    // Mosaic Tiles: Clicking any mosaic tile loads corresponding preset
    mosaicTiles.forEach((tile) => {
      tile.addEventListener('click', () => {
        const index = parseInt(tile.getAttribute('data-preset-index') || '0', 10);
        if (presetsData && presetsData[index]) {
          const btn = presetContainer.children[index];
          applyPreset(presetsData[index], btn);
        } else {
          // Fallback if presets endpoint had an issue
          const company = tile.querySelector('.company-name')?.textContent || 'Target Company';
          const role = tile.querySelector('.role-name')?.textContent || 'Target Role';
          jobTitleInput.value = role;
          companyInput.value = company;
        }

        const workspaceElem = document.getElementById('workspace');
        if (workspaceElem) {
          workspaceElem.scrollIntoView({ behavior: 'smooth', block: 'start' });
        }
      });
    });
  }

  // -------------------------------------------------------------
  // 3. Top Banner, Header, and Auth Modal Interactions
  // -------------------------------------------------------------
  function initHeaderInteractions() {
    // Dismiss announcement banner
    if (announcementCloseBtn && announcementBar) {
      announcementCloseBtn.addEventListener('click', () => {
        announcementBar.style.maxHeight = '0px';
        announcementBar.style.padding = '0px';
        announcementBar.style.overflow = 'hidden';
        announcementBar.style.opacity = '0';
        setTimeout(() => announcementBar.classList.add('hidden'), 300);
      });
    }

    // Mobile Hamburger Menu Toggle
    if (mobileMenuToggle && mobileNavDrawer) {
      mobileMenuToggle.addEventListener('click', () => {
        const isHidden = mobileNavDrawer.classList.contains('hidden');
        if (isHidden) {
          mobileNavDrawer.classList.remove('hidden');
          mobileMenuToggle.setAttribute('aria-expanded', 'true');
        } else {
          mobileNavDrawer.classList.add('hidden');
          mobileMenuToggle.setAttribute('aria-expanded', 'false');
        }
      });

      // Close mobile drawer on link click
      document.querySelectorAll('.mobile-nav-link').forEach(link => {
        link.addEventListener('click', () => {
          mobileNavDrawer.classList.add('hidden');
          mobileMenuToggle.setAttribute('aria-expanded', 'false');
        });
      });
    }

    // History Buttons
    if (viewHistoryBtn) viewHistoryBtn.addEventListener('click', openHistory);
    if (mobileHistoryBtn) {
      mobileHistoryBtn.addEventListener('click', () => {
        if (mobileNavDrawer) mobileNavDrawer.classList.add('hidden');
        openHistory();
      });
    }

    // Auth Modal Handlers (Using the Editorial Template for JobMatch AI)
    const openAuth = (mode) => {
      if (!authModal) return;
      authModal.classList.remove('hidden');
      const sub = document.getElementById('auth-modal-subtitle');
      if (mode === 'login') {
        if (authModalTitle) authModalTitle.textContent = 'Hello Again!';
        if (sub) sub.textContent = "Sign in to access your saved matches & resumes";
      } else {
        if (authModalTitle) authModalTitle.textContent = 'Hello Again!';
        if (sub) sub.textContent = "Let's get started with your 30 days trial";
      }
    };

    if (headerLoginBtn) {
      headerLoginBtn.addEventListener('click', (e) => {
        e.preventDefault();
        openAuth('login');
      });
    }
    if (headerGetStartedBtn) {
      headerGetStartedBtn.addEventListener('click', (e) => {
        e.preventDefault();
        openAuth('register');
      });
    }
    if (mobileLoginBtn) {
      mobileLoginBtn.addEventListener('click', (e) => {
        e.preventDefault();
        if (mobileNavDrawer) mobileNavDrawer.classList.add('hidden');
        openAuth('login');
      });
    }
    if (mobileGetStartedBtn) {
      mobileGetStartedBtn.addEventListener('click', (e) => {
        e.preventDefault();
        if (mobileNavDrawer) mobileNavDrawer.classList.add('hidden');
        openAuth('register');
      });
    }

    if (closeAuthBtn) {
      closeAuthBtn.addEventListener('click', () => authModal.classList.add('hidden'));
    }

    if (authModal) {
      authModal.addEventListener('click', (e) => {
        if (e.target === authModal) authModal.classList.add('hidden');
      });
    }

    // Modal Password Visibility Toggle
    const modalPasswordToggle = document.getElementById('modal-password-toggle');
    const modalPasswordInput = document.getElementById('auth-password');
    if (modalPasswordToggle && modalPasswordInput) {
      const eyeOff = modalPasswordToggle.querySelector('.eye-off');
      const eyeOn = modalPasswordToggle.querySelector('.eye-on');

      modalPasswordToggle.addEventListener('click', () => {
        const isPassword = modalPasswordInput.type === 'password';
        modalPasswordInput.type = isPassword ? 'text' : 'password';
        if (eyeOff) eyeOff.classList.toggle('hidden', isPassword);
        if (eyeOn) eyeOn.classList.toggle('hidden', !isPassword);
      });
    }

    // Modal Quotes Carousel
    const modalQuotes = [
      "Finally, all your work in one place.",
      "Match your resume to your dream job in seconds.",
      "AI-powered skill gap roadmaps for your engineering career.",
      "Neural ATS scoring with Apache PDFBox precision."
    ];
    let modalQuoteIdx = 0;
    const modalQuoteElem = document.getElementById('modal-quote-text');
    const modalPrevBtn = document.getElementById('modal-carousel-prev');
    const modalNextBtn = document.getElementById('modal-carousel-next');

    function updateModalQuote(idx) {
      if (!modalQuoteElem) return;
      modalQuoteElem.style.opacity = '0';
      modalQuoteElem.style.transform = 'translateY(5px)';
      setTimeout(() => {
        modalQuoteElem.textContent = modalQuotes[idx];
        modalQuoteElem.style.opacity = '1';
        modalQuoteElem.style.transform = 'translateY(0)';
      }, 200);
    }

    if (modalPrevBtn) {
      modalPrevBtn.addEventListener('click', () => {
        modalQuoteIdx = (modalQuoteIdx - 1 + modalQuotes.length) % modalQuotes.length;
        updateModalQuote(modalQuoteIdx);
      });
    }

    if (modalNextBtn) {
      modalNextBtn.addEventListener('click', () => {
        modalQuoteIdx = (modalQuoteIdx + 1) % modalQuotes.length;
        updateModalQuote(modalQuoteIdx);
      });
    }

    // Social buttons in modal
    ['modal-social-google', 'modal-social-apple', 'modal-social-facebook'].forEach(id => {
      const btn = document.getElementById(id);
      if (btn) {
        btn.addEventListener('click', () => {
          const provider = id.replace('modal-social-', '');
          alert(`Redirecting to ${provider.toUpperCase()} authentication...`);
        });
      }
    });

    const modalRecoveryLink = document.getElementById('modal-recovery-link');
    if (modalRecoveryLink) {
      modalRecoveryLink.addEventListener('click', (e) => {
        e.preventDefault();
        const email = document.getElementById('auth-email')?.value || 'your email';
        alert(`Password reset instructions sent to ${email}.`);
      });
    }

    if (authForm) {
      authForm.addEventListener('submit', (e) => {
        e.preventDefault();
        const email = document.getElementById('auth-email').value;
        alert(`Welcome to JobMatch AI, ${email}! Instant session active.`);
        authModal.classList.add('hidden');
      });
    }
  }

  // -------------------------------------------------------------
  // 4. Tab Switching
  // -------------------------------------------------------------
  tabPdf.addEventListener('click', () => switchTab('pdf'));
  tabText.addEventListener('click', () => switchTab('text'));

  function switchTab(tab) {
    activeTab = tab;
    if (tab === 'pdf') {
      tabPdf.classList.add('active');
      tabText.classList.remove('active');
      panePdf.classList.add('active');
      paneText.classList.remove('active');
    } else {
      tabText.classList.add('active');
      tabPdf.classList.remove('active');
      paneText.classList.add('active');
      panePdf.classList.remove('active');
    }
  }

  // -------------------------------------------------------------
  // 5. PDF Drag & Drop and Upload (Apache PDFBox)
  // -------------------------------------------------------------
  ['dragenter', 'dragover'].forEach(eventName => {
    dropZone.addEventListener(eventName, (e) => {
      e.preventDefault();
      dropZone.classList.add('dragover');
    });
  });

  ['dragleave', 'drop'].forEach(eventName => {
    dropZone.addEventListener(eventName, (e) => {
      e.preventDefault();
      dropZone.classList.remove('dragover');
    });
  });

  dropZone.addEventListener('drop', (e) => {
    const files = e.dataTransfer.files;
    if (files.length > 0) {
      handleFileSelected(files[0]);
    }
  });

  dropZone.addEventListener('click', (e) => {
    // Only open if clicking on dropzone directly, not remove button
    if (!e.target.closest('#remove-file-btn')) {
      pdfFileInput.click();
    }
  });

  pdfFileInput.addEventListener('change', (e) => {
    if (e.target.files.length > 0) {
      handleFileSelected(e.target.files[0]);
    }
  });

  function handleFileSelected(file) {
    if (!file.name.toLowerCase().endsWith('.pdf')) {
      alert('Please select a valid PDF file.');
      return;
    }

    selectedPdfFile = file;
    selectedFileName.textContent = file.name;
    selectedFileSize.textContent = formatBytes(file.size);

    dropZoneContent.classList.add('hidden');
    filePreviewCard.classList.remove('hidden');

    // Auto-extract text preview via PDFBox
    extractPdfPreview(file);
  }

  removeFileBtn.addEventListener('click', (e) => {
    e.stopPropagation();
    selectedPdfFile = null;
    pdfFileInput.value = '';
    filePreviewCard.classList.add('hidden');
    dropZoneContent.classList.remove('hidden');
    extractedPreviewWrapper.classList.add('hidden');
    extractedPreviewText.textContent = '';
  });

  async function extractPdfPreview(file) {
    extractedPreviewWrapper.classList.remove('hidden');
    extractedPreviewText.textContent = 'Extracting resume text with Apache PDFBox...';

    const formData = new FormData();
    formData.append('file', file);

    try {
      const res = await fetch('/api/resume/upload', {
        method: 'POST',
        body: formData
      });

      if (!res.ok) {
        const err = await res.json();
        throw new Error(err.error || 'Failed to extract text');
      }

      const data = await res.json();
      extractedPreviewText.textContent = data.extractedText || 'No text found in PDF.';
      const words = (data.extractedText || '').trim().split(/\s+/).filter(Boolean).length;
      extractedWordCount.textContent = `${words} words extracted`;

      if (data.candidateName && !candidateNameInput.value) {
        candidateNameInput.value = data.candidateName;
      }
    } catch (e) {
      extractedPreviewText.textContent = `Error extracting PDF: ${e.message}`;
    }
  }

  // -------------------------------------------------------------
  // 6. Text Handlers & Character Counters
  // -------------------------------------------------------------
  resumeTextInput.addEventListener('input', () => updateCharCount(resumeTextInput, resumeCharCount));
  jobDescInput.addEventListener('input', () => updateCharCount(jobDescInput, jobCharCount));

  function updateCharCount(textarea, counterElem) {
    const len = textarea.value.length;
    counterElem.textContent = `${len.toLocaleString()} characters`;
  }

  clearResumeBtn.addEventListener('click', () => {
    resumeTextInput.value = '';
    updateCharCount(resumeTextInput, resumeCharCount);
  });

  clearJobBtn.addEventListener('click', () => {
    jobDescInput.value = '';
    updateCharCount(jobDescInput, jobCharCount);
  });

  resetAllBtn.addEventListener('click', () => {
    resumeTextInput.value = '';
    jobDescInput.value = '';
    jobTitleInput.value = '';
    companyInput.value = '';
    candidateNameInput.value = '';
    removeFileBtn.click();
    updateCharCount(resumeTextInput, resumeCharCount);
    updateCharCount(jobDescInput, jobCharCount);
    resultsSection.classList.add('hidden');
    document.querySelectorAll('.preset-btn').forEach(b => b.classList.remove('active'));
  });

  // -------------------------------------------------------------
  // 7. Match Analysis Submission
  // -------------------------------------------------------------
  analyzeBtn.addEventListener('click', async () => {
    const jobDescription = jobDescInput.value.trim();
    if (!jobDescription) {
      alert('Please enter a Job Description to match against.');
      jobDescInput.focus();
      return;
    }

    let isPdf = activeTab === 'pdf' && selectedPdfFile != null;
    let resumeText = resumeTextInput.value.trim();

    if (!isPdf && !resumeText) {
      alert('Please upload a PDF resume or enter resume text.');
      return;
    }

    setLoading(true);

    try {
      let matchResponse;

      if (isPdf) {
        // Multipart Upload Match
        const formData = new FormData();
        formData.append('file', selectedPdfFile);
        formData.append('jobDescription', jobDescription);
        formData.append('jobTitle', jobTitleInput.value.trim() || 'Software Engineer');
        formData.append('company', companyInput.value.trim() || 'Tech Company');
        if (candidateNameInput.value.trim()) {
          formData.append('candidateName', candidateNameInput.value.trim());
        }

        const res = await fetch('/api/match/upload', {
          method: 'POST',
          body: formData
        });

        if (!res.ok) {
          const err = await res.json();
          throw new Error(err.error || 'Matching failed');
        }
        matchResponse = await res.json();
      } else {
        // Text JSON Match
        const payload = {
          resumeText: resumeText,
          jobDescription: jobDescription,
          jobTitle: jobTitleInput.value.trim() || 'Software Engineer',
          company: companyInput.value.trim() || 'Tech Company',
          candidateName: candidateNameInput.value.trim() || 'Candidate'
        };

        const res = await fetch('/api/match', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify(payload)
        });

        if (!res.ok) {
          const err = await res.json();
          throw new Error(err.error || 'Matching failed');
        }
        matchResponse = await res.json();
      }

      renderMatchResults(matchResponse);
    } catch (e) {
      alert(`Matching failed: ${e.message}`);
    } finally {
      setLoading(false);
    }
  });

  function setLoading(loading) {
    if (loading) {
      analyzeBtn.disabled = true;
      analyzeSpinner.classList.remove('hidden');
    } else {
      analyzeBtn.disabled = false;
      analyzeSpinner.classList.add('hidden');
    }
  }

  // -------------------------------------------------------------
  // 8. Render Match Results
  // -------------------------------------------------------------
  function renderMatchResults(data) {
    resultsSection.classList.remove('hidden');

    // Title info
    resultsTargetInfo.textContent = `Candidate: ${data.candidateName || 'Candidate'} → Target Role: ${data.jobTitle || 'Role'} at ${data.company || 'Employer'}`;

    // Animate Circular Gauge
    const score = Math.round(data.overallScore || 0);
    animateScore(score);

    // Verdict Badge
    verdictBadge.textContent = data.verdict || 'Match Complete';
    verdictDesc.textContent = data.verdictDescription || '';
    applyVerdictStyle(verdictBadge, score);

    // Breakdown Bars
    const skillScore = Math.round(data.skillMatchScore || 0);
    const tfidfScore = Math.round(data.tfidfScore || 0);
    const semanticScore = Math.round(data.semanticScore || 0);

    metricSkillVal.textContent = `${skillScore}%`;
    barSkill.style.width = `${skillScore}%`;
    metricSkillSub.textContent = `${data.totalMatchedSkills} of ${data.totalRequiredSkills} required skills satisfied`;

    metricTfidfVal.textContent = `${tfidfScore}%`;
    barTfidf.style.width = `${tfidfScore}%`;

    metricSemanticVal.textContent = `${semanticScore}%`;
    barSemantic.style.width = `${semanticScore}%`;

    // Counts
    countMatched.textContent = data.totalMatchedSkills || 0;
    countMissing.textContent = data.totalMissingSkills || 0;
    countExtra.textContent = (data.extraSkills || []).length;

    // Matched Skills Tags
    matchedSkillsContainer.innerHTML = '';
    if (!data.matchedSkills || data.matchedSkills.length === 0) {
      matchedSkillsContainer.innerHTML = '<span style="color: #64748b; font-size: 13px;">No direct skill matches detected.</span>';
    } else {
      data.matchedSkills.forEach(skill => {
        const tag = document.createElement('div');
        const isSemantic = skill.relevanceWeight < 0.99 && skill.relatedTo;
        tag.className = `skill-tag ${isSemantic ? 'tag-semantic' : 'tag-matched'}`;

        const iconSvg = isSemantic
          ? `<svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><path d="M12 2v20M17 5H9.5a3.5 3.5 0 0 0 0 7h5a3.5 3.5 0 0 1 0 7H6"/></svg>`
          : `<svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><polyline points="20 6 9 17 4 12"></polyline></svg>`;

        let extraContent = '';
        if (isSemantic) {
          extraContent = `<span class="tag-semantic-badge" title="Related to resume skill: ${skill.relatedTo}">~ via ${skill.relatedTo} (${Math.round(skill.relevanceWeight * 100)}%)</span>`;
        }

        tag.innerHTML = `
          ${iconSvg}
          <span>${skill.name}</span>
          <span class="tag-category-pill">${formatCategory(skill.category)}</span>
          ${extraContent}
        `;
        matchedSkillsContainer.appendChild(tag);
      });
    }

    // Missing Skills Tags
    missingSkillsContainer.innerHTML = '';
    if (!data.missingSkills || data.missingSkills.length === 0) {
      missingSkillsContainer.innerHTML = '<span style="color: #10b981; font-size: 13px;">No skill gaps! All required skills are present.</span>';
    } else {
      data.missingSkills.forEach(skill => {
        const tag = document.createElement('div');
        tag.className = 'skill-tag tag-missing';
        tag.innerHTML = `
          <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5">
            <line x1="18" y1="6" x2="6" y2="18"></line>
            <line x1="6" y1="6" x2="18" y2="18"></line>
          </svg>
          <span>${skill.name}</span>
          <span class="tag-category-pill">${formatCategory(skill.category)}</span>
        `;
        missingSkillsContainer.appendChild(tag);
      });
    }

    // Extra Applicant Skills
    extraSkillsContainer.innerHTML = '';
    if (!data.extraSkills || data.extraSkills.length === 0) {
      extraSkillsCard.classList.add('hidden');
    } else {
      extraSkillsCard.classList.remove('hidden');
      data.extraSkills.forEach(skill => {
        const tag = document.createElement('div');
        tag.className = 'skill-tag tag-extra';
        tag.innerHTML = `
          <svg width="12" height="12" viewBox="0 0 24 24" fill="currentColor">
            <polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"></polygon>
          </svg>
          <span>${skill.name}</span>
          <span class="tag-category-pill">${formatCategory(skill.category)}</span>
        `;
        extraSkillsContainer.appendChild(tag);
      });
    }

    // Roadmap Timeline
    roadmapTimeline.innerHTML = '';
    if (!data.roadmap || data.roadmap.length === 0) {
      roadmapTimeline.innerHTML = '<div style="color: #10b981; padding: 10px 0;">🎉 Congratulations! No required skill gaps were found for this position.</div>';
    } else {
      data.roadmap.forEach(step => {
        const item = document.createElement('div');
        item.className = 'timeline-item';

        const priorityClass = step.priority && step.priority.toLowerCase().includes('critical')
          ? 'priority-critical'
          : step.priority && step.priority.toLowerCase().includes('high')
          ? 'priority-high'
          : 'priority-advanced';

        const topicsHtml = (step.recommendedTopics || [])
          .map(topic => `<span class="topic-chip">${topic}</span>`)
          .join('');

        item.innerHTML = `
          <div class="timeline-number-circle">${step.stepNumber}</div>
          <div class="timeline-card">
            <div class="timeline-header">
              <div class="timeline-skill-title">
                <span>Learn ${step.skillName}</span>
                <span class="priority-badge ${priorityClass}">${step.priority || 'High Priority'}</span>
              </div>
              <span class="timeline-time">Estimated: ${step.estimatedTime || '1-2 weeks'}</span>
            </div>
            <p class="timeline-rationale">${step.rationale || ''}</p>
            <div class="timeline-topics-group">
              ${topicsHtml}
            </div>
          </div>
        `;
        roadmapTimeline.appendChild(item);
      });
    }

    // Scroll smoothly to results
    resultsSection.scrollIntoView({ behavior: 'smooth', block: 'start' });
  }

  function animateScore(targetScore) {
    const circumference = 427.26; // 2 * Math.PI * 68
    const offset = circumference - (targetScore / 100) * circumference;

    // Color gradient based on score
    let strokeColor = '#10b981';
    if (targetScore < 50) strokeColor = '#f43f5e';
    else if (targetScore < 75) strokeColor = '#f59e0b';

    gaugeFillCircle.style.stroke = strokeColor;
    gaugeFillCircle.style.strokeDashoffset = offset;

    // Counter animation
    let current = 0;
    const duration = 1200;
    const stepTime = 20;
    const steps = duration / stepTime;
    const increment = targetScore / steps;

    const timer = setInterval(() => {
      current += increment;
      if (current >= targetScore) {
        current = targetScore;
        clearInterval(timer);
      }
      scorePercentVal.textContent = `${Math.round(current)}%`;
    }, stepTime);
  }

  function applyVerdictStyle(badgeElem, score) {
    if (score >= 80) {
      badgeElem.style.background = 'rgba(16, 185, 129, 0.15)';
      badgeElem.style.borderColor = 'rgba(16, 185, 129, 0.4)';
      badgeElem.style.color = '#34d399';
    } else if (score >= 60) {
      badgeElem.style.background = 'rgba(245, 158, 11, 0.15)';
      badgeElem.style.borderColor = 'rgba(245, 158, 11, 0.4)';
      badgeElem.style.color = '#fde68a';
    } else {
      badgeElem.style.background = 'rgba(244, 63, 94, 0.15)';
      badgeElem.style.borderColor = 'rgba(244, 63, 94, 0.4)';
      badgeElem.style.color = '#fda4af';
    }
  }

  function formatCategory(cat) {
    if (!cat) return '';
    const map = {
      'PROGRAMMING_LANGUAGE': 'Language',
      'FRAMEWORK': 'Framework',
      'DATABASE': 'Database',
      'CLOUD_DEVOPS': 'Cloud/DevOps',
      'ARCHITECTURE_CONCEPT': 'Concept',
      'AI_DATA': 'AI/Data',
      'TESTING_TOOL': 'Tool'
    };
    return map[cat] || cat;
  }

  function formatBytes(bytes) {
    if (bytes === 0) return '0 Bytes';
    const k = 1024;
    const sizes = ['Bytes', 'KB', 'MB', 'GB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    return parseFloat((bytes / Math.pow(k, i)).toFixed(1)) + ' ' + sizes[i];
  }

  // -------------------------------------------------------------
  // 9. History Drawer
  // -------------------------------------------------------------
  closeDrawerBtn.addEventListener('click', closeHistory);
  historyDrawerOverlay.addEventListener('click', (e) => {
    if (e.target === historyDrawerOverlay) closeHistory();
  });

  async function openHistory() {
    historyDrawerOverlay.classList.remove('hidden');
    historyListContainer.innerHTML = '<div class="history-empty">Loading history...</div>';

    try {
      const res = await fetch('/api/match/history');
      if (!res.ok) throw new Error('Failed to fetch history');
      const items = await res.json();

      if (!items || items.length === 0) {
        historyListContainer.innerHTML = '<div class="history-empty">No match history yet. Run a match to see saved reports here.</div>';
        return;
      }

      historyListContainer.innerHTML = '';
      items.forEach(item => {
        const div = document.createElement('div');
        div.className = 'history-item';
        div.innerHTML = `
          <div class="history-header">
            <span class="history-title">${item.candidateName || 'Candidate'}</span>
            <span class="history-score" style="color: ${item.matchScore >= 70 ? '#10b981' : item.matchScore >= 50 ? '#f59e0b' : '#f43f5e'};">
              ${Math.round(item.matchScore)}% Match
            </span>
          </div>
          <div class="history-meta">
            <span>${item.jobTitle || 'Role'} • ${item.createdAt || ''}</span>
          </div>
        `;
        div.addEventListener('click', () => loadHistoricalMatch(item.id));
        historyListContainer.appendChild(div);
      });
    } catch (e) {
      historyListContainer.innerHTML = `<div class="history-empty">Error loading history: ${e.message}</div>`;
    }
  }

  function closeHistory() {
    historyDrawerOverlay.classList.add('hidden');
  }

  async function loadHistoricalMatch(matchId) {
    try {
      const res = await fetch(`/api/match/${matchId}`);
      if (!res.ok) throw new Error('Could not load report');
      const data = await res.json();
      closeHistory();
      renderMatchResults(data);
    } catch (e) {
      alert(`Failed to load historical match: ${e.message}`);
    }
  }

  // Print / Save Report
  printReportBtn.addEventListener('click', () => {
    window.print();
  });
});
