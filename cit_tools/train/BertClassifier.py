import pandas as pd
import numpy as np
import torch
import torch.nn as nn
from transformers import AutoTokenizer, AutoModelForSequenceClassification, AdamW, Trainer, TrainingArguments
from sklearn.model_selection import train_test_split
from sklearn.metrics import accuracy_score, classification_report
from sklearn.metrics import precision_score,recall_score,f1_score,precision_recall_fscore_support
from torch.utils.tensorboard import SummaryWriter
from torch.utils.data import DataLoader
from transformers import AutoModel, AutoConfig
from sklearn.metrics import classification_report,confusion_matrix
import itertools
from matplotlib import pyplot as plt

# plot confusion matrix

project_dir = "/Users/ludab/Laptop/project2024/ai-document-tools/cit_tools/"
#test_name = "intent_zukerberg"
test_name = "intent_zukerberg_sent"

def plot_confusion_matrix(cm, classes,
                          normalize=False,
                          title='Confusion matrix',
                          cmap=plt.cm.Blues):
    """
    This function prints and plots the confusion matrix.
    Normalization can be applied by setting `normalize=True`.
    """
    if normalize:
        cm = cm.astype('float') / cm.sum(axis=1)[:, np.newaxis]
        print("Normalized confusion matrix")
    else:
        print('Confusion matrix, without normalization')

    print(cm)

    plt.imshow(cm, interpolation='nearest', cmap=cmap)
    plt.title(title)
    plt.colorbar()
    tick_marks = np.arange(len(classes))
    plt.xticks(tick_marks, classes, rotation=45)
    plt.yticks(tick_marks, classes)

    fmt = '.2f' if normalize else 'd'
    thresh = cm.max() / 2.
    for i, j in itertools.product(range(cm.shape[0]), range(cm.shape[1])):
        plt.text(j, i, format(cm[i, j], fmt),
                 horizontalalignment="center",
                 color="white" if cm[i, j] > thresh else "black")

    plt.tight_layout()
    plt.ylabel('Human label')
    plt.xlabel('Predicted label')
    plt.show(block=False)
    #plt.savefig('/Users/ludab/Laptop/project2023/urldiscovery/plots/arxiv/confusematrix_3contextNov_14.png')
    plt.savefig(project_dir+'/model_results/confusematrix_'+test_name+'.png')
    
def text_converter(value):
    try:
        return str(value).strip().lower()
    except:
        return ''
def data_clean(feature_data):
    feature_data = feature_data.apply(lambda x: text_converter(x))
    #feature_data = feature_data.apply(lambda x: x.lower())
    return feature_data

from sklearn.metrics import accuracy_score, precision_recall_fscore_support

def compute_metrics(p):
    pred, labels = p
    #label_values =['dataset','software','media','paper']
    label_values = [ "created",  "used"]
    pred = np.argmax(pred, axis=1)
    
    #f1_micro_average = f1_score(y_true=labels, y_pred=pred, average='micro')
    #roc_auc = roc_auc_score(labels, pred, average = 'micro')
    
    accuracy = accuracy_score(y_true=labels, y_pred=pred)

    # Calculate precision, recall, and F1-score per class
    precision, recall, f1, _ = precision_recall_fscore_support(y_true=labels, y_pred=pred, average=None)
    for i in range(len(precision)):
            print(f'Metrics/precision_class_{i}', precision[i])
            print(f'Metrics/recall_class_{i}', recall[i])
            print(f'Metrics/f1_class_{i}', f1[i])
    # Calculate the weighted metrics as well
    weighted_precision, weighted_recall, weighted_f1, _ = precision_recall_fscore_support(y_true=labels, y_pred=pred, average='weighted')
    print(classification_report(labels, pred, target_names=[str(l) for l in label_values]))
    report = classification_report(labels, pred, output_dict=True)
    df = pd.DataFrame(report).transpose()
    df.to_csv(project_dir+'/model_results/test_score'+test_name+'.csv') 
    cm_test = confusion_matrix(labels, pred)

    np.set_printoptions(precision=2)

    plt.figure(figsize=(4,4))
    plot_confusion_matrix(cm_test, classes=label_values, title='Confusion Matrix')
    
    # Return the metrics as a dictionary
    metrics = {
        "accuracy": accuracy,
       # "classprecision": precision.tolist(),  # List of per-class precisions
       # "classrecall": recall.tolist(),  # List of per-class recalls
       # "classf1": f1.tolist(),  # List of per-class F1-scores
        "precision": weighted_precision,
        "recall": weighted_recall,
        "f1": weighted_f1,
    }

    return metrics

   
def compute_metrics_(p):
    pred, labels = p
    pred = np.argmax(pred, axis=1)

    accuracy = accuracy_score(y_true=labels, y_pred=pred)
    recall = recall_score(y_true=labels, y_pred=pred,average='weighted')
    precision = precision_score(y_true=labels, y_pred=pred,average='weighted')
    f1 = f1_score(y_true=labels, y_pred=pred,average='weighted')

    return {"accuracy": accuracy, "precision": precision, "recall": recall, "f1": f1}
# Load the CSV dataset (assuming it has 'text' and 'label' columns)

def train_model ():
      
    dataset_path = project_dir+"/datasets/software_citation_intent_merged.csv"
    
    data_types = {
        'id' : str,
        'sentence': str,
        'used': str,
        'created' : str,
        'mention':  str,  
        'context': str,
        'label' :int,
        'text': str
        
        
        #'other_column_name': float,  # Replace with other column names and appropriate data types
        # ...
    }
    df1 = pd.read_csv(dataset_path,dtype=data_types)
    num2label = {0: "created", 1: "used", 2: "mention", 3: "none"}
    label2num = {"created": 0, "used": 1, "mention": 2, "none": 3}
    type_counts = df1['label'].value_counts()
    print(type_counts)
    
    df = df1.loc[~((df1['label'] == 3))]
    
    # Assuming df is your DataFrame
    df['label0'] = df.apply(lambda row: 0 if row['label'] == 0 else 1, axis=1)
    type_counts = df['label0'].value_counts()
    print(type_counts)

    #df['text'] = data_clean(df['text'])
    
    # Create a SummaryWriter object
    writer = SummaryWriter(log_dir='./logs')
    
    def g(df):
        #This line creates the training set by randomly sampling 70% of the rows from the input DataFrame df. The frac parameter specifies the fraction of rows to be sampled, and random_state is set to 42 to ensure that the random sampling is reproducible (i.e., it will generate the same random sample every time you run the code with the same seed).
        train_df = df.sample(frac=0.7, random_state=42)
        #This line creates the test set by dropping the rows that are already included in the training set. It uses the .drop() method with the indices of the rows in the training set (train_df.index) to exclude those rows from the original DataFrame, resulting in the test set.
        test_df = df.drop(train_df.index)
        train_df = train_df.sample(frac=1, random_state=42) #.head(3000)
        #This line shuffles the rows in the training set to ensure randomness. It sets frac to 1, meaning it takes all the rows in the training set, and the random_state is the same as before (42) to ensure the same shuffle order each time you run the code.
        test_df = test_df.sample(frac=1, random_state=42) #.head(3000)
        return train_df, test_df
    
    train_df, test_df = g(df.copy())
    print(train_df)
    print(test_df)
    df_union = pd.concat([train_df, test_df], ignore_index=True)
    train_df, test_df = train_test_split(df_union, test_size=0.2, random_state=42)
    type(train_df)
    # Initialize the SciBERT tokenizer and model
    model_name = "allenai/scibert_scivocab_uncased"
    #model_name = "distilbert-base-uncased"

    #config = AutoConfig.from_pretrained(model_name)
    #base_model = AutoModel.from_pretrained(model_name, config=config)
    tokenizer = AutoTokenizer.from_pretrained(model_name,model_max_length=512)
    #num_labels=len(df['label'].unique()
    num_labels=2             
    model = AutoModelForSequenceClassification.from_pretrained(model_name, num_labels=2)
    
    
    # Tokenize the input data
    train_encodings = tokenizer(list(train_df['sentence']), truncation=True, padding=True)
    test_encodings = tokenizer(list(test_df['sentence']), truncation=True, padding=True)
    
    # Convert the labels to tensors
    train_labels = torch.tensor(list(train_df['label0']))
    test_labels = torch.tensor(list(test_df['label0']))
    
    # Create a PyTorch dataset
    from torch.utils.data import Dataset
    
    class CustomDataset(Dataset):
        def __init__(self, encodings, labels):
            self.encodings = encodings
            self.labels = labels
    
        def __getitem__(self, idx):
            item = {key: torch.tensor(val[idx]) for key, val in self.encodings.items()}
            item['labels'] = torch.tensor(self.labels[idx])
            return item
    
        def __len__(self):
            return len(self.labels)
    
    train_dataset = CustomDataset(train_encodings, train_labels)
    test_dataset = CustomDataset(test_encodings, test_labels)
    
    # Define training arguments
    training_args = TrainingArguments(
        output_dir='./results',
        num_train_epochs=4,
        per_device_train_batch_size=8,
        per_device_eval_batch_size=8,
        warmup_steps=500,
        weight_decay=0.01,
        #learning_rate = 2e-5,
        logging_dir='./logs',
        #evaluation_strategy="epoch",
        logging_strategy="epoch",
        save_total_limit=2,
        do_predict = True,
        do_eval=True,
        evaluation_strategy="steps",
        #load_best_model_at_end=True,
        
        #label_names = ["0", "1"]
    )
    
    # Initialize the Trainer
    trainer = Trainer(
        model=model,
        args=training_args,
        train_dataset=train_dataset,
        eval_dataset=test_dataset,
        compute_metrics = compute_metrics
    )
    print("loaded")
    validation_losses = []
    # Start training the model
    for epoch in range(training_args.num_train_epochs):
    
        # Fine-tune the model
        trainer.train()
    
        # Evaluate the model on the test set
        print("epoch",epoch)
        results = trainer.evaluate()
        print("Results:", results)
        
        # Log the loss and accuracy to TensorBoard
        writer.add_scalar('Loss/Validation', results['eval_loss'], global_step=epoch)
        #writer.add_scalar('Loss/Train', results['train_loss'], global_step=epoch)
        writer.add_scalar('accuracy', results['eval_accuracy'], global_step=epoch)
        writer.add_scalar('f1', results['eval_f1'], global_step=epoch)
        writer.add_scalar('precision', results['eval_precision'], global_step=epoch)
        writer.add_scalar('recall', results['eval_recall'], global_step=epoch)
        # Collect the training and validation loss
        #train_loss = results['train_loss']
        validation_loss = results['eval_loss']
       # train_losses.append(train_loss)
        validation_losses.append(validation_loss)

    
    # Save the model
    print("saving model")
    model.save_pretrained(project_dir+/models/scibert_finetuned_4_5000_2_"+test_name)
    # Close the tensorboard writer
    writer.close()
   
    
    train_loss = []
    for elem in trainer.state.log_history:
     if 'loss' in elem.keys():
        train_loss.append(elem['loss'])
    
    epochs = list(range(1, training_args.num_train_epochs + 1))
    plt.figure(figsize=(10, 6))
    plt.plot(epochs, train_loss, label='Training Loss', marker='o')
    plt.plot(epochs, validation_losses, label='Validation Loss', marker='o')
    plt.xlabel('Epochs')
    plt.ylabel('Loss')
    plt.title('Training and Validation Loss Over Epochs')
    plt.legend()
    plt.grid(True)
    plt.savefig(project_dir+'/model_results/loss_plot'+test_name+'.png')
    # Save the training and validation loss values to a file
    with open(project_dir+'/model_results/loss_values_'+test_name+'.txt', 'w') as file:
        file.write('Epoch\tTraining Loss\tValidation Loss\n')
        for epoch, train_loss_val, val_loss_val in zip(epochs, train_loss, validation_losses):
            file.write(f'{epoch}\t{train_loss_val}\t{val_loss_val}\n')

    # Save the plot as an image or display it
    #plt.savefig('/Users/ludab/Laptop/project2023/urldiscovery/plots/cm/loss_plot'+test_name+'.png')  # Save as an image
    plt.show()  # Display the plot
    
train_model ()





        
        
